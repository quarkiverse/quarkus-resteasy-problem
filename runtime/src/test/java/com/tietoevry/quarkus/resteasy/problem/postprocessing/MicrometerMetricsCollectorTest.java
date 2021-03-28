package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import static com.tietoevry.quarkus.resteasy.problem.ProblemMother.badRequestProblem;
import static com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.zalando.problem.ProblemBuilder;

class MicrometerMetricsCollectorTest {

    ProblemPostProcessor processor = new MicrometerMetricsCollector();

    @Test
    void shouldNotChangeProblemBuilder() {
        ProblemBuilder originalProblem = badRequestProblem();

        ProblemBuilder enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem).isEqualTo(originalProblem);
    }

}
