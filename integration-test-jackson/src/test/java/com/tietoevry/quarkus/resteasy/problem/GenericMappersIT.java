package com.tietoevry.quarkus.resteasy.problem;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GenericMappersIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void httpProblemShouldReturnHeaders() {
        when()
                .get("/throw/generic/http-problem")
                .then()
                .statusCode(409)
                .header("X-RFC7807", equalTo("IsAlive"))
                .body("title", equalTo("Product is out of stock"))
                .body("status", equalTo(409))
                .body("product", equalTo("rfc7807"))
                .body("stacktrace", nullValue());
    }

}
