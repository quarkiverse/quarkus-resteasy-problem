package io.quarkiverse.resteasy.problem;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Integration test to verify that ONLY runtime configuration works when build-time config is absent.
 * This test ensures the compatibility layer functions correctly by using deprecated runtime config properties.
 *
 *
 * NOTE: This test uses getConfigOverrides() which simulates runtime configuration.
 * The deprecation warning will appear in the application startup logs during the test.
 */
@QuarkusTest
@TestProfile(ConstraintViolationLegacyRuntimeConfigIT.RuntimeConfigOnlyProfile.class)
class ConstraintViolationLegacyRuntimeConfigIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void runtimeConfigOnlyShouldWork() {
        given()
                .body("{\"phraseName\": 10 }")
                .contentType(APPLICATION_JSON)
                .post("/throw/validation/constraint-violation-exception")
                .then()
                .statusCode(422)
                .body("title", equalTo("Runtime Config Title"))
                .body("status", equalTo(422))
                .body("violations", hasSize(1));
    }

    public static class RuntimeConfigOnlyProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "quarkus.resteasy.problem.constraint-violation.status", "422",
                    "quarkus.resteasy.problem.constraint-violation.title", "Runtime Config Title",
                    "quarkus.resteasy.problem.constraint-violation.description", "This proves runtime config works"
                    
                    // Explicitly verify NO build-time config is set by not including any
                    // quarkus.resteasy.problem.constraint-violation.* properties would be build-time if they existed
            );
        }
    }
}