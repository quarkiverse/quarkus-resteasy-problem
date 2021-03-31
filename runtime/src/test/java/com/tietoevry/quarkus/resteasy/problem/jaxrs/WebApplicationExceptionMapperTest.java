package com.tietoevry.quarkus.resteasy.problem.jaxrs;

import static com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase.APPLICATION_PROBLEM_JSON;
import static javax.ws.rs.core.HttpHeaders.RETRY_AFTER;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

class WebApplicationExceptionMapperTest {

    static final MediaType MEDIA_TYPE_SHOULD_BE_IGNORED = MediaType.TEXT_PLAIN_TYPE;

    WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper();

    @Test
    void shouldMapAllBasicFields() {
        WebApplicationException exception = new WebApplicationException("Hello world", 418);

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(418);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_PROBLEM_JSON);
        assertThat(response.getEntity())
                .isInstanceOf(Problem.class)
                .hasFieldOrPropertyWithValue("detail", "Hello world");
    }

    @Test
    void shouldPassOnHeadersButIgnoreMediaType() {
        WebApplicationException exception = new WebApplicationException(
                Response.status(429)
                        .header(RETRY_AFTER, 120)
                        .type(MEDIA_TYPE_SHOULD_BE_IGNORED)
                        .build());

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_PROBLEM_JSON);
        assertThat(response.getHeaderString(RETRY_AFTER)).isEqualTo("120");
        assertThat(response.getEntity())
                .isInstanceOf(Problem.class)
                .hasFieldOrPropertyWithValue("detail", "HTTP 429 Too Many Requests");
    }

    @Test
    void shouldMapRedirectionException() {
        WebApplicationException exception = new RedirectionException(
                Response.Status.MOVED_PERMANENTLY,
                URI.create("/new-location"));

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(301);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_PROBLEM_JSON);
        assertThat(response.getHeaderString("Location")).endsWith("/new-location");
        assertThat(response.getEntity())
                .isInstanceOf(Problem.class)
                .hasFieldOrPropertyWithValue("detail", "HTTP 301 Moved Permanently");
    }
}
