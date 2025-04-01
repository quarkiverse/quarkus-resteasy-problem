package io.quarkiverse.resteasy.problem.openapi;

import java.net.URI;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "HttpProblem", description = "HTTP Problem Response according to RFC9457 & RFC7807")
public class HttpProblemSchema {

    public static final String MEDIA_TYPE = "application/problem+json";

    @Schema(description = "A URI reference that identifies the problem type", examples = "https://example.com/errors/validation")
    URI type;

    /**
     * A short, human-readable summary of the problem type
     */
    @Schema(description = "A short, human-readable summary of the problem type", examples = "Not Found Error")
    String title;

    /**
     * The HTTP status code for this occurrence of the problem
     */
    @Schema(description = "The HTTP status code for this occurrence of the problem", examples = "400")
    int status;

    /**
     * A human-readable explanation specific to this occurrence of the problem
     */
    @Schema(description = "A human-readable explanation specific to this occurrence of the problem", examples = "Record not found")
    String detail;

    /**
     * A URI reference that identifies the specific occurrence of the problem
     */
    @Schema(description = "A URI reference that identifies the specific occurrence of the problem", examples = "https://api.example.com/errors/123")
    URI instance;

    /** Additional parameters providing more details about the problem */
    // TODO this needs some thinking
    //@JsonProperty("context")
    ///@Schema(description = "Additional parameters providing more details about the problem", examples = "{\"timestamp\":\"2024-03-20T10:00:00Z\",\"traceId\":\"550e8400-e29b-41d4-a716-446655440000\"}")
    //private SortedMap<String, Object> contexts;

}
