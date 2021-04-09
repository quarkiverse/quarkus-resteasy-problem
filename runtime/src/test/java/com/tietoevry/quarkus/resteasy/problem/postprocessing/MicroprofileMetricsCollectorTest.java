package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import static com.tietoevry.quarkus.resteasy.problem.HttpProblemMother.badRequestProblem;
import static com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import org.junit.jupiter.api.Test;

class MicroprofileMetricsCollectorTest {

    ProblemPostProcessor processor = new MicroprofileMetricsCollector();

    @Test
    void shouldNotChangeProblemBuilder() {
        HttpProblem originalProblem = badRequestProblem();

        HttpProblem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem).isEqualTo(originalProblem);
    }

}
