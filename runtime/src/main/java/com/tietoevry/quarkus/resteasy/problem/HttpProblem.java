package com.tietoevry.quarkus.resteasy.problem;

import io.smallrye.common.constraint.Nullable;
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
import javax.annotation.concurrent.Immutable;
import org.zalando.problem.Problem;
import org.zalando.problem.StatusType;

/**
 * Extension to Zalando's Problem interface, which allows creating and throwing Problems with additional HTTP headers to be
 * included in final HTTP response.
 *
 * It can also replace Zalando's Problem class in the future releases, so that we can get rid of this dependency.
 */
@Immutable
public final class HttpProblem extends RuntimeException implements Problem {

    private final URI type;
    private final String title;
    private final StatusType status;
    private final String detail;
    private final URI instance;
    private final Map<String, Object> parameters;
    private final Map<String, Object> headers;

    private HttpProblem(@Nullable final URI type,
            @Nullable final String title,
            @Nullable final StatusType status,
            @Nullable final String detail,
            @Nullable final URI instance,
            @Nullable final Map<String, Object> parameters,
            @Nullable final Map<String, Object> headers) {
        super();
        this.type = Optional.ofNullable(type).orElse(DEFAULT_TYPE);
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.parameters = Collections.unmodifiableMap(Optional.ofNullable(parameters).orElseGet(LinkedHashMap::new));
        this.headers = Collections.unmodifiableMap(headers);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Kind of copy constructor
     *
     * @param origin
     * @return Builder object with values taken from origin HttpProblem
     */
    public static Builder builder(HttpProblem origin) {
        Builder builder = builder()
                .withType(origin.getType())
                .withInstance(origin.getInstance())
                .withTitle(origin.getTitle())
                .withStatus(origin.getStatus())
                .withDetail(origin.getDetail());
        origin.parameters.forEach(builder::with);
        origin.headers.forEach(builder::withHeader);
        return builder;
    }

    @Override
    public URI getType() {
        return this.type;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public StatusType getStatus() {
        return this.status;
    }

    @Override
    public String getDetail() {
        return this.detail;
    }

    @Override
    public URI getInstance() {
        return this.instance;
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public Map<String, Object> getHeaders() {
        return this.headers;
    }

    @Override
    public String getMessage() {
        return Stream.of(this.getTitle(), this.getDetail()).filter(Objects::nonNull).collect(Collectors.joining(": "));
    }

    @Override
    public String toString() {
        return Problem.toString(this);
    }

    public static class Builder {

        private static final Set<String> RESERVED_PROPERTIES = new HashSet<>(Arrays.asList(
                "type", "title", "status", "detail", "instance", "cause"));

        private URI type;
        private String title;
        private StatusType status;
        private String detail;
        private URI instance;
        private final Map<String, Object> headers = new LinkedHashMap<>();
        private final Map<String, Object> parameters = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder withType(@Nullable final URI type) {
            this.type = type;
            return this;
        }

        public Builder withTitle(@Nullable final String title) {
            this.title = title;
            return this;
        }

        public Builder withStatus(@Nullable final StatusType status) {
            this.status = status;
            return this;
        }

        public Builder withDetail(@Nullable final String detail) {
            this.detail = detail;
            return this;
        }

        public Builder withInstance(@Nullable final URI instance) {
            this.instance = instance;
            return this;
        }

        public Builder withHeader(final String headerName, final Object value) {
            headers.put(headerName, value);
            return this;
        }

        /**
         * @throws IllegalArgumentException if key is any of type, title, status, detail or instance
         */
        public Builder with(final String key, final Object value) throws IllegalArgumentException {
            if (RESERVED_PROPERTIES.contains(key)) {
                throw new IllegalArgumentException("Property " + key + " is reserved");
            }
            parameters.put(key, value);
            return this;
        }

        public HttpProblem build() {
            return new HttpProblem(type, title, status, detail, instance, new LinkedHashMap<>(parameters),
                    new LinkedHashMap<>(headers));
        }

    }

}
