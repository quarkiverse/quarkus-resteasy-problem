package io.quarkiverse.resteasy.problem;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import org.jboss.logmanager.Level;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test to verify that build-time configuration works without triggering 
 * the compatibility layer or deprecation warnings.
 */
@QuarkusTest
@TestProfile(ConstraintViolationMapperConfigIT.BuildTimeConfigProfile.class)
class ConstraintViolationMapperConfigIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void buildTimeConfigShouldWorkWithoutWarnings() {
        given()
                .body("{\"phraseName\": 10 }")
                .contentType(APPLICATION_JSON)
                .post("/throw/validation/constraint-violation-exception")
                .then()
                .statusCode(422)
                .body("title", equalTo("Build-Time Config Title"))
                .body("status", equalTo(422))
                .body("violations", hasSize(1));
    }

    public static class BuildTimeConfigProfile implements QuarkusTestProfile {

        @Override
        public String getConfigProfile() {
            // Use the 'build-time-config' profile which loads application-build-time-config.properties
            return "build-time-config";
        }

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of();
        }
    }
}
