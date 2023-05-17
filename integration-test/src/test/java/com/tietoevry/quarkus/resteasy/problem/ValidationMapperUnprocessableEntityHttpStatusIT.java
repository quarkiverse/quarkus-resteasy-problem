package com.tietoevry.quarkus.resteasy.problem;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static com.tietoevry.quarkus.resteasy.problem.ExtendedStatus.UNPROCESSABLE_ENTITY;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
@TestProfile(UnprocessableEntityHttpStatusProfile.class)
class ValidationMapperUnprocessableEntityHttpStatusIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void mapperUsesConfiguredHttpStatusCode() {
        given()
                .body("{\"phraseName\": 10 }")
                .contentType(APPLICATION_JSON)
                .post("/throw/validation/constraint-violation-exception")
                .then()
                .statusCode(UNPROCESSABLE_ENTITY.getStatusCode())
                .body("title", equalTo(UNPROCESSABLE_ENTITY.getReasonPhrase()))
                .body("status", equalTo(UNPROCESSABLE_ENTITY.getStatusCode()))
                .body("violations", hasSize(1))
                .body("violations[0].field", equalTo("phraseName"))
                .body("violations[0].message", equalTo("must be greater than or equal to 15"))
                .body("stacktrace", nullValue());
    }
}
