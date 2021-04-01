package com.tietoevry.quarkus.resteasy.problem;

import static com.tietoevry.quarkus.resteasy.problem.ProblemContextMother.simpleContext;
import static com.tietoevry.quarkus.resteasy.problem.ProblemMother.badRequestProblem;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

class MicrometerMetricsCollectorTest {

    ProblemPostProcessor processor = new MicrometerMetricsCollector();

    @Test
    void shouldNotChangeProblemBuilder() {
        Problem originalProblem = badRequestProblem();

        Problem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem).isEqualTo(originalProblem);
    }

}
