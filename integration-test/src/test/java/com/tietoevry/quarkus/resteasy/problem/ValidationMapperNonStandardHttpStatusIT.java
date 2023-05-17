package com.tietoevry.quarkus.resteasy.problem;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
@TestProfile(NonStandardHttpStatusProfile.class)
class ValidationMapperNonStandardHttpStatusIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void mapperFallsBackToDefaultHttpStatus() {
        given()
                .body("{\"phraseName\": 10 }")
                .contentType(APPLICATION_JSON)
                .post("/throw/validation/constraint-violation-exception")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .body("title", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(BAD_REQUEST.getStatusCode()));
    }
}
