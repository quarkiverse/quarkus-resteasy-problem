package com.tietoevry.quarkus.resteasy.problem;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@QuarkusTest
class XmlProblemIT {

    static final String SAMPLE_DETAIL = "A small one";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void runtimeExceptionShouldReturn500() {
        given()
                .accept(ContentType.XML)
                .queryParam("message", SAMPLE_DETAIL)
                .get("/throw/generic/runtime-exception")
                .then()
                .log().all()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .contentType("application/problem+xml")
                .body("problem.title", equalTo(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body("problem.status", equalTo("500"));
    }

    @Test
    void additionalParametersShouldBeSerializedProperly() {
        given()
                .accept(ContentType.XML)
                .body("{\"key\": 10 }")
                .contentType(APPLICATION_JSON)
                .post("/throw/javax/constraint-violation-exception")
                .then()
                .statusCode(400)
                .contentType("application/problem+xml")
                .body("problem.violations.field", equalTo("key"))
                .body("problem.violations.error", equalTo("must be greater than or equal to 15"));
    }
}