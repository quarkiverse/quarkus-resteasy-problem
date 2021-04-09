package com.tietoevry.quarkus.resteasy.problem;

import static com.tietoevry.quarkus.resteasy.problem.HttpProblemMother.complexProblem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpProblemTest {

    @Test
    void builderShouldPassAllFields() {
        HttpProblem problem = complexProblem().build();

        assertThat(problem.getType()).hasHost("tietoevry.com").hasPath("/problem");
        assertThat(problem.getInstance()).hasPath("/endpoint");
        assertThat(problem.getStatus().getStatusCode()).isEqualTo(400);
        assertThat(problem.getDetail()).isEqualTo("Deep down wrongness, zażółć gęślą jaźń for Håkensth");
        assertThat(problem.getHeaders())
                .containsEntry("X-Numeric-Header", 123)
                .containsEntry("X-String-Header", "ABC");
    }

    @Test
    void copyBuilderShouldCopyAllFields() {
        HttpProblem original = complexProblem().build();

        HttpProblem copy = HttpProblem.builder(original).build();

        assertThat(copy)
                .isEqualToComparingFieldByField(original);
    }

    @Test
    void valueOfShouldSetTitle() {
        assertThat(HttpProblem.valueOf(Response.Status.BAD_REQUEST).getTitle())
                .isEqualTo(Response.Status.BAD_REQUEST.getReasonPhrase());

        assertThat(HttpProblem.valueOf(Response.Status.BAD_REQUEST, "Some detail").getTitle())
                .isEqualTo(Response.Status.BAD_REQUEST.getReasonPhrase());
    }

    @ParameterizedTest()
    @ValueSource(strings = { "type", "instance", "detail", "status" })
    void builderShouldNotAllowAddingReservedProperties(String property) {
        assertThatThrownBy(() -> HttpProblem.builder().with(property, "not relevant"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
