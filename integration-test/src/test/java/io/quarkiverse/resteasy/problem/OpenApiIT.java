package io.quarkiverse.resteasy.problem;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
class OpenApiIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void shouldGenerateValidOpenApiDescriptorForHttpProblemResponsesEvenIfNotExplicitlyDefined() {
        given()
                .accept(ContentType.JSON)
                .get("/q/openapi")
                .then()
                .statusCode(200)
                .body("paths./openapi/hello-world.post.responses.403.description", equalTo("Hello received but rejected"))
                // RestAssured's JsonPath does not like in application/problem+json, so bracket notation is the only way
                .body("paths['/openapi/hello-world']['post']['responses']['403']['content']['application/problem+json'].schema.$ref", equalTo("#/components/schemas/HttpProblem"))
                .body("components.schemas.HttpProblem.description", equalTo("HTTP Problem Response according to RFC9457 & RFC7807"))
                .body("components.schemas.HttpProblem.properties.title.description", equalTo("A short, human-readable summary of the problem type"));
    }

}
