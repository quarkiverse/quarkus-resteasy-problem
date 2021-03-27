package com.tietoevry.quarkus.resteasy.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.problem.Status.I_AM_A_TEAPOT;
import static org.zalando.problem.Status.REQUEST_URI_TOO_LONG;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;

class ProblemDefaultsProviderTest {

    ProblemDefaultsProvider processor = new ProblemDefaultsProvider("/errors/");

    @Test
    void shouldProvideDefaults() {
        Problem originalProblem = exampleProblemBuilder().build();

        Problem enhancedProblem = processor.apply(originalProblem, ProblemContextMother.simple());

        assertThat(enhancedProblem.getType()).hasPath("/errors/bad-request");
        assertThat(enhancedProblem.getInstance()).hasPath("/endpoint");
    }

    @Test
    void typeShouldHandleNonStandardCharacters() {
        Problem requestUriTooLong = processor.apply(Problem.valueOf(REQUEST_URI_TOO_LONG), ProblemContextMother.simple());
        assertThat(requestUriTooLong.getType()).hasPath("/errors/request-uri-too-long");

        Problem iAmTeapot = processor.apply(Problem.valueOf(I_AM_A_TEAPOT), ProblemContextMother.simple());
        assertThat(iAmTeapot.getType()).hasPath("/errors/im-a-teapot");
    }

    @Test
    void shouldNotOverrideExistingValues() {
        Problem originalProblem = exampleProblemBuilder()
                .withType(URI.create("/business-error"))
                .withInstance(URI.create("/non-default-endpoint"))
                .build();

        Problem enhancedProblem = processor.apply(originalProblem, ProblemContextMother.simple());

        assertThat(enhancedProblem.getType()).hasPath("/business-error");
        assertThat(enhancedProblem.getInstance()).hasPath("/non-default-endpoint");
    }

    private ProblemBuilder exampleProblemBuilder() {
        return Problem.builder()
                .withTitle("title")
                .withStatus(Status.BAD_REQUEST);
    }

}
