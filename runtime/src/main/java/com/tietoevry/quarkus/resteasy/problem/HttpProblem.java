package com.tietoevry.quarkus.resteasy.problem;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Representation of RFC7807 Problem schema.
 */
@Immutable
public class HttpProblem extends RuntimeException {

    public static final MediaType MEDIA_TYPE = new MediaType("application", "problem+json");

    private final URI type;
    private final String title;
    private final Response.StatusType status;
    private final String detail;
    private final URI instance;
    private final Map<String, Object> parameters;
    private final Map<String, Object> headers;

    protected HttpProblem(Builder builder) {
        super(createMessage(builder.title, builder.detail));

        this.type = builder.type;
        this.title = builder.title;
        this.status = Optional.ofNullable(builder.status).orElse(INTERNAL_SERVER_ERROR);
        this.detail = builder.detail;
        this.instance = builder.instance;
        this.parameters = Collections.unmodifiableMap(Optional.ofNullable(builder.parameters).orElseGet(LinkedHashMap::new));
        this.headers = Collections.unmodifiableMap(Optional.ofNullable(builder.headers).orElseGet(LinkedHashMap::new));
    }

    private static String createMessage(String title, String detail) {
        return Stream.of(title, detail)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(": "));
    }

    public static HttpProblem valueOf(Response.Status status) {
        return builder()
                .withTitle(status.getReasonPhrase())
                .withStatus(status)
                .build();
    }

    public static HttpProblem valueOf(Response.Status status, String detail) {
        return builder()
                .withTitle(status.getReasonPhrase())
                .withStatus(status)
                .withDetail(detail)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates Builder instance and initializes it with fields from given HttpProblem
     *
     * @param original Problem 'prototype'
     * @return Builder object with values taken from origin HttpProblem
     */
    public static Builder builder(HttpProblem original) {
        Builder builder = builder()
                .withType(original.getType())
                .withInstance(original.getInstance())
                .withTitle(original.getTitle())
                .withStatus(original.getStatus())
                .withDetail(original.getDetail());
        original.parameters.forEach(builder::with);
        original.headers.forEach(builder::withHeader);
        return builder;
    }

    public URI getType() {
        return this.type;
    }

    public String getTitle() {
        return this.title;
    }

    public Response.StatusType getStatus() {
        return this.status;
    }

    public String getDetail() {
        return this.detail;
    }

    public URI getInstance() {
        return this.instance;
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public Map<String, Object> getHeaders() {
        return this.headers;
    }

    public static class Builder {

        private static final Set<String> RESERVED_PROPERTIES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                "type", "title", "status", "detail", "instance")));

        private URI type;
        private String title;
        private Response.StatusType status;
        private String detail;
        private URI instance;
        private final Map<String, Object> headers = new LinkedHashMap<>();
        private final Map<String, Object> parameters = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder withType(@Nullable URI type) {
            this.type = type;
            return this;
        }

        public Builder withTitle(@Nullable String title) {
            this.title = title;
            return this;
        }

        public Builder withStatus(@Nullable Response.StatusType status) {
            this.status = status;
            return this;
        }

        public Builder withStatus(int statusCode) {
            this.status = Response.Status.fromStatusCode(statusCode);
            return this;
        }

        public Builder withDetail(@Nullable String detail) {
            this.detail = detail;
            return this;
        }

        public Builder withInstance(@Nullable URI instance) {
            this.instance = instance;
            return this;
        }

        public Builder withHeader(String headerName, Object value) {
            headers.put(headerName, value);
            return this;
        }

        /**
         * @throws IllegalArgumentException if key is any of type, title, status, detail or instance
         */
        public Builder with(String key, Object value) throws IllegalArgumentException {
            if (RESERVED_PROPERTIES.contains(key)) {
                throw new IllegalArgumentException("Property " + key + " is reserved");
            }
            parameters.put(key, value);
            return this;
        }

        public HttpProblem build() {
            return new HttpProblem(this);
        }

    }

}
