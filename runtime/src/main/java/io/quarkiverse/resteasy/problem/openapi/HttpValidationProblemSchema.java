package io.quarkiverse.resteasy.problem.openapi;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "HttpValidationProblem", description = "HTTP Validation Problem Response according to RFC9457 & RFC7807", additionalProperties = Schema.True.class)
public class HttpValidationProblemSchema extends HttpProblemSchema {

    /**
     * List of validation violations that occurred
     */
    @Schema(description = "List of validation constraint violations that occurred")
    private List<ViolationSchema> violations;

    @Schema(name = "Violation", description = "Validation constraint violation details")
    public static class ViolationSchema {

        /**
         * The field for which the validation failed
         */
        @Schema(description = "The field for which the validation failed", examples = "#/profile/email")
        String field;

        /**
         * Part of the http request where the validation error occurred
         */
        @Schema(description = "Part of the http request where the validation error occurred such as query, path, header, form, body", examples = {
                "query", "path", "header", "form", "body" })
        String in;

        /**
         * Description of the validation error
         */
        @Schema(description = "Description of the validation error", examples = "Invalid email format")
        String detail;
    }
}
