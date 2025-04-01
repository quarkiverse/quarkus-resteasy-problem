package io.quarkiverse.resteasy.problem.deployment;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;

import io.quarkiverse.resteasy.problem.openapi.HttpProblemSchema;
import io.smallrye.openapi.internal.models.media.Content;
import io.smallrye.openapi.internal.models.media.MediaType;
import io.smallrye.openapi.internal.models.media.Schema;

/**
 * OpenAPI filter that automatically adds Problem Details schema to error responses.
 * This filter runs at build time and enhances the OpenAPI documentation by adding
 * the HttpProblem schema reference to any 4xx or 5xx response that doesn't already
 * have content defined.
 */
public class OpenApiProblemFilter implements OASFilter {

    private static final Content PROBLEM_CONTENT = createDefaultContent();
    private static final org.eclipse.microprofile.openapi.models.media.Schema MDC_PROPERTY_SCHEMA = new Schema()
            .addType(Schema.SchemaType.STRING)
            .set("description", "Additional context of the problem");

    private final ProblemBuildConfig config;

    public OpenApiProblemFilter(ProblemBuildConfig config) {
        this.config = config;
    }

    /**
     * Filters API responses to add Problem Details schema for 4xx and 5xx error responses that don't have explicit
     *
     * @Content defined in @ApiResponse.
     */
    @Override
    public APIResponse filterAPIResponse(APIResponse apiResponse) {
        if (apiResponse == null || apiResponse.getRef() != null || apiResponse.getContent() != null) {
            return apiResponse;
        }

        if (!(apiResponse instanceof io.smallrye.openapi.internal.models.responses.APIResponse internalResponse)) {
            return apiResponse;
        }

        String responseCode = (String) internalResponse.getExtension("x-smallrye-private-response-code");
        if (responseCode == null || responseCode.isEmpty()) {
            return apiResponse;
        }

        try {
            int httpStatus = Integer.parseInt(responseCode);
            if (httpStatus >= 400) {
                apiResponse.setContent(PROBLEM_CONTENT);
            }
        } catch (NumberFormatException e) {
            return apiResponse;
        }

        return apiResponse;
    }

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        if (config.includeMdcProperties().isEmpty()) {
            return;
        }

        org.eclipse.microprofile.openapi.models.media.Schema httpProblemSchema = openAPI.getComponents().getSchemas()
                .get("HttpProblem");
        org.eclipse.microprofile.openapi.models.media.Schema httpValidationProblem = openAPI.getComponents().getSchemas()
                .get("HttpValidationProblem");

        config.includeMdcProperties().forEach(mdcProperty -> {
            httpProblemSchema.addProperty(mdcProperty, MDC_PROPERTY_SCHEMA);
            httpValidationProblem.addProperty(mdcProperty, MDC_PROPERTY_SCHEMA);
        });
    }

    /**
     * Creates the default content for Problem Details responses.
     * Sets up the media type and schema reference for HttpProblem.
     *
     * @return Content object configured for Problem Details
     */
    private static Content createDefaultContent() {
        Content content = new Content();

        MediaType mediaType = new MediaType();
        content.addMediaType(HttpProblemSchema.MEDIA_TYPE, mediaType);

        Schema schema = new Schema();
        schema.setRef("#/components/schemas/HttpProblem");
        mediaType.setSchema(schema);

        return content;
    }
}
