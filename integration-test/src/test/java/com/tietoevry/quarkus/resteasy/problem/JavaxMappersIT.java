package com.tietoevry.quarkus.resteasy.problem;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.zalando.problem.Status.BAD_REQUEST;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
class JavaxMappersIT {

    static final String SAMPLE_DETAIL = "A small one";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void violationShouldReturnDetails() {
        given()
                .queryParam("message", SAMPLE_DETAIL)
                .get("/throw/javax/violation-exception")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .body("title", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(BAD_REQUEST.getStatusCode()))
                .body("detail", equalTo(SAMPLE_DETAIL))
                .body("stacktrace", nullValue());
    }

    @Test
    void constraintViolationShouldProvideErrorDetails() {
        given()
                .body("{\"key\": 10 }")
                .contentType(APPLICATION_JSON)
                .post("/throw/javax/constraint-violation-exception")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .body("title", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(BAD_REQUEST.getStatusCode()))
                .body("violations", hasSize(1))
                .body("violations[0].field", equalTo("key"))
                .body("violations[0].error", equalTo("must be greater than or equal to 15"))
                .body("stacktrace", nullValue());
    }

}
