package com.tietoevry.quarkus.resteasy.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.zalando.problem.Status;

class HttpProblemTest {

    @Test
    void emptyProblemShouldHaveTypeAboutBlank() {
        HttpProblem problem = HttpProblem.builder().build();

        assertThat(problem.getType().toString()).isEqualTo("about:blank");
    }

    @Test
    void builderShouldPassAllFields() {
        HttpProblem problem = sampleHttpProblem();

        assertThat(problem.getType())
                .hasHost("tietoevry.com")
                .hasPath("/problem");

        assertThat(problem.getStatus().getStatusCode()).isEqualTo(400);
        assertThat(problem.getDetail()).isEqualTo("This is detail");

        assertThat(problem.getHeaders())
                .containsEntry("X-Numeric-Header", 123)
                .containsEntry("X-String-Header", "ABC");
    }

    @Test
    void copyBuilderShouldCopyAllFields() {
        HttpProblem original = sampleHttpProblem();

        HttpProblem copy = HttpProblem.builder(original).build();

        assertThat(copy)
                .isEqualToComparingFieldByField(original);
    }

    @ParameterizedTest()
    @ValueSource(strings = { "type", "instance", "detail", "status" })
    void builderShouldNotAllowAddingReservedProperties(String property) {
        assertThatThrownBy(() -> HttpProblem.builder().with(property, "not relevant"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private HttpProblem sampleHttpProblem() {
        return HttpProblem.builder()
                .withType(URI.create("http://tietoevry.com/problem"))
                .withStatus(Status.BAD_REQUEST)
                .withTitle("This is title")
                .withDetail("This is detail")
                .with("customField", "value")
                .withHeader("X-Numeric-Header", 123)
                .withHeader("X-String-Header", "ABC")
                .build();
    }

}
