package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import static com.tietoevry.quarkus.resteasy.problem.ProblemMother.badRequestProblem;
import static com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

class ProblemDefaultsProviderTest {

    ProblemDefaultsProvider processor = new ProblemDefaultsProvider();

    @Test
    void shouldProvideDefaults() {
        Problem enhancedProblem = processor.apply(badRequestProblem(), simpleContext()).build();

        assertThat(enhancedProblem.getInstance()).hasPath("/endpoint");
    }

    @Test
    void shouldNotOverrideExistingValues() {
        ProblemBuilder originalProblem = badRequestProblem()
                .withType(URI.create("/business-error"))
                .withInstance(URI.create("/non-default-endpoint"));

        Problem enhancedProblem = processor.apply(originalProblem, simpleContext()).build();

        assertThat(enhancedProblem.getInstance()).hasPath("/non-default-endpoint");
    }

}
