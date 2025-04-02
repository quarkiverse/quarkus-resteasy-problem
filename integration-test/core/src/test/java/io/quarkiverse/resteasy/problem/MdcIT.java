package io.quarkiverse.resteasy.problem;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.util.Map;

@QuarkusTest
@TestProfile(MdcIT.Config.class)
class MdcIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void mdcPropertiesShouldBeReturned() {
        given()
                .get("/throw/mdc")
                .then()
                .body("uuid", equalTo("30a48c9e"))
                .body("field-from-configuration", equalTo("123"));
    }

    public static class Config implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
            "quarkus.resteasy.problem.include-mdc-properties", "uuid,field-from-configuration,another-field"
            );
        }
    }
}
