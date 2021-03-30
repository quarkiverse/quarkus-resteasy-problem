package com.tietoevry.quarkus.resteasy.problem;

import static com.tietoevry.quarkus.resteasy.problem.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;

class ProblemDefaultsProviderTest {

    ProblemDefaultsProvider processor = new ProblemDefaultsProvider();

    @Test
    void shouldProvideDefaults() {
        Problem originalProblem = exampleProblemBuilder().build();

        Problem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem.getInstance()).hasPath("/endpoint");
    }

    @Test
    void shouldNotOverrideExistingValues() {
        Problem originalProblem = exampleProblemBuilder()
                .withInstance(URI.create("/non-default-endpoint"))
                .build();

        Problem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem.getInstance()).hasPath("/non-default-endpoint");
    }

    private ProblemBuilder exampleProblemBuilder() {
        return Problem.builder()
                .withTitle("title")
                .withStatus(Status.BAD_REQUEST);
    }

}
