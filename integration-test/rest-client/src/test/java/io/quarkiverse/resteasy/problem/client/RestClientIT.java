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

    @Test
    void shouldCallItselfViaRestApiAndRethrowHttpProblemWithOverridenInstance() {
        given()
                .accept(ContentType.JSON)
                .get("/throw-via-rest-client")
                .then()
                .statusCode(418)
                .body("title", equalTo("I'm a teapot"))
                .body("detail", equalTo("Nothing to add"))
                .body("instance", equalTo("/throw-via-rest-client"));
    }

}
