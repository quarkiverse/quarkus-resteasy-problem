package io.quarkiverse.resteasy.problem;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
class MetricsIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @ParameterizedTest(name = "http status: {0}")
    @ValueSource(ints = { 400, 401, 403, 404, 500, 502, 511 })
    void httpErrorMetricShouldBeIncreased(int status) {
        throwException(status);
        int initialMetricValue = fetchHttpErrorMetric(status);

        throwException(status);

        int newMetricValue = fetchHttpErrorMetric(status);
        assertThat(newMetricValue).isGreaterThan(initialMetricValue);
    }

    private int fetchHttpErrorMetric(int status) {
        ExtractableResponse response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/q/metrics/application/http.error")
                .then()
                .extract();

        if (response.statusCode() != 200) {
            return 0;
        }

        Map<String, Object> appMetrics = response.path("$");
        return (int) appMetrics.getOrDefault("http.error;status=" + status, 0);
    }

    private void throwException(int httpStatus) {
        given()
                .queryParam("status", httpStatus)
                .get("/throw/jax-rs/web-application-exception")
                .then();
    }

}
