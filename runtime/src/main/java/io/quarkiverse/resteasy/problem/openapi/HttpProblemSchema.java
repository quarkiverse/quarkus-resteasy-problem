package io.quarkiverse.resteasy.problem.openapi;

import java.net.URI;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "HttpProblem", description = "HTTP Problem Response according to RFC9457 & RFC7807", additionalProperties = Schema.True.class)
public class HttpProblemSchema {

    public static final String MEDIA_TYPE = "application/problem+json";

    @Schema(description = "A optional URI reference that identifies the problem type", examples = "https://example.com/errors/not-found")
    URI type;

    /**
     * A short, human-readable summary of the problem type
     */
    @Schema(description = "A optional, short, human-readable summary of the problem type", examples = "Not Found")
    String title;

    /**
     * The HTTP status code for this occurrence of the problem
     */
    @Schema(description = "The HTTP status code for this occurrence of the problem", examples = "404")
    int status;

    /**
     * A human-readable explanation specific to this occurrence of the problem
     */
    @Schema(description = "A optional human-readable explanation specific to this occurrence of the problem", examples = "Record not found")
    String detail;

    /**
     * A URI reference that identifies the specific occurrence of the problem
     */
    @Schema(description = "A URI reference that identifies the specific occurrence of the problem", examples = "https://api.example.com/errors/123")
    URI instance;

}
