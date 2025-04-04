package io.quarkiverse.resteasy.problem.client;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
class RestClientIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * Reproducer for https://github.com/quarkiverse/quarkus-resteasy-problem/issues/429
     * <p>
     * Problem dissapears when `quarkus-resteasy-problem` is not in the classpath, but the reason why it happens is unclear.
     * <p>
     * Also, the issue exists only when using reactive rest stack, it works fine in classic (blocking) mode.
     * <p>
     * Adding ClientExceptionMapper to RestClient fixes the problem, but IMO it's not a fix, but rather walkaround.
     */
    @Test
    void shouldCallItselfViaRestApiWithoutClientExceptionMapper() {
        given()
                .accept(ContentType.JSON)
                .get("/throw-via-rest-client")
                .then()
                .statusCode(409)
                .body("title", equalTo("Conflict"))
                .body("detail", equalTo("HTTP 409 Conflict"))
                .body("instance", equalTo("/throw-via-rest-client"));
    }

    @Test
    void shouldRethrowHttpProblemFromUpstreamServiceWithOverriddenInstance() {
        given()
                .accept(ContentType.JSON)
                .get("/throw-via-rest-client-with-mapper")
                .then()
                .statusCode(409)
                .body("title", equalTo("Conflict from upstream service"))
                .body("detail", equalTo("Nothing to add"))
                .body("instance", equalTo("/throw-via-rest-client-with-mapper"));
    }

}
