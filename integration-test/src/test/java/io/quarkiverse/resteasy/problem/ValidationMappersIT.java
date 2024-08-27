package io.quarkiverse.resteasy.problem;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ValidationMappersIT {

    static final String SAMPLE_DETAIL = "A small one";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void validationExceptionShouldReturn500() {
        given()
                .queryParam("message", SAMPLE_DETAIL)
                .get("/throw/validation/validation-exception")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body("title", equalTo(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body("status", equalTo(INTERNAL_SERVER_ERROR.getStatusCode()))
                .body("detail", nullValue())
                .body("stacktrace", nullValue());
    }

    @Test
    void constraintDeclarationExceptionShouldReturn500() {
        given()
                .queryParam("message", SAMPLE_DETAIL)
                .get("/throw/validation/constraint-declaration-exception")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body("title", equalTo(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body("status", equalTo(INTERNAL_SERVER_ERROR.getStatusCode()))
                .body("detail", nullValue())
                .body("stacktrace", nullValue());
    }

    @Test
    void constraintViolationShouldProvideErrorDetails() {
        given()
                .body("{\"phraseName\": 10 }")
                .contentType(APPLICATION_JSON)
                .post("/throw/validation/constraint-violation-exception")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .body("title", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(BAD_REQUEST.getStatusCode()))
                .body("violations", hasSize(1))
                .body("violations[0].field", equalTo("phraseName"))
                .body("violations[0].message", equalTo("must be greater than or equal to 15"))
                .body("stacktrace", nullValue());
    }

    @Test
    void constraintViolationForArgumentsShouldProvideErrorDetails() {
        given()
                .contentType(APPLICATION_JSON)
                .body("{\"phraseName\": 1}")
                .queryParam("param_name", "invalidQueryParam")
                .queryParam("param_name2", "validQueryParam")
                .header("param_name3", "invalidHeaderParam")
                .pathParam("param_name4", "invalidPathParam")
                .post("/throw/validation/constraint-violation-exception/{param_name4}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .body("title", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(BAD_REQUEST.getStatusCode()))
                .body("violations", hasSize(4))
                .body("violations.find{it.in == 'query'}.field", equalTo("param_name"))
                .body("violations.find{it.in == 'query'}.message", equalTo("length must be between 10 and 15"))
                .body("violations.find{it.in == 'header'}.field", equalTo("param_name3"))
                .body("violations.find{it.in == 'header'}.message", equalTo("length must be between 10 and 15"))
                .body("violations.find{it.in == 'path'}.field", equalTo("param_name4"))
                .body("violations.find{it.in == 'path'}.message", equalTo("length must be between 10 and 15"))
                .body("violations.find{it.in == 'body'}.field", equalTo("phraseName"))
                .body("violations.find{it.in == 'body'}.message", equalTo("must be greater than or equal to 15"));
    }

}
