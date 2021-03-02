package com.tietoevry.quarkus.resteasy.problem;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.zalando.problem.Status;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.zalando.problem.Status.FORBIDDEN;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static org.zalando.problem.Status.NOT_FOUND;

@QuarkusTest
class JaxRsMappersIT {

    static final int INVALID_HTTP_CODE = 701;
    static final String SAMPLE_DETAIL = "A small one";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @ParameterizedTest(name = "http status: {0}")
    @ValueSource(ints = {400, 401, 403, 404, 500, 502, 511})
    void webApplicationExceptionShouldReturnGivenStatus(int status) {
        given()
                .queryParam("status", status)
                .get("/throw/jax-rs/web-application-exception")
                .then()
                .statusCode(status)
                .body("title", equalTo(Status.valueOf(status).getReasonPhrase()))
                .body("status", equalTo(status))
                .body("stacktrace", nullValue());
    }

    @Test
    void webApplicationExceptionWithInvalidCodeShould500() {
        given()
                .queryParam("status", INVALID_HTTP_CODE)
                .get("/throw/jax-rs/web-application-exception")
                .then()
                .statusCode(500)
                .body("title", equalTo(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body("status", equalTo(INTERNAL_SERVER_ERROR.getStatusCode()))
                .body("stacktrace", nullValue());
    }


    @Test
    void jaxRsNotFoundShouldReturn404ProblemInsteadOfDefaultRestEasyDefaultResponse() {
        given()
                .queryParam("message", SAMPLE_DETAIL)
                .get("/throw/jax-rs/not-found-exception")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .body("title", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("status", equalTo(NOT_FOUND.getStatusCode()))
                .body("detail", equalTo(SAMPLE_DETAIL))
                .body("stacktrace", nullValue());
    }


    @Test
    void jaxRsForbiddenShouldReturn403ProblemInsteadOfDefaultRestEasyDefaultResponse() {
        given()
                .queryParam("message", SAMPLE_DETAIL)
                .get("/throw/jax-rs/forbidden-exception")
                .then()
                .statusCode(FORBIDDEN.getStatusCode())
                .body("title", equalTo(FORBIDDEN.getReasonPhrase()))
                .body("status", equalTo(FORBIDDEN.getStatusCode()))
                .body("detail", equalTo(SAMPLE_DETAIL))
                .body("stacktrace", nullValue());
    }

}