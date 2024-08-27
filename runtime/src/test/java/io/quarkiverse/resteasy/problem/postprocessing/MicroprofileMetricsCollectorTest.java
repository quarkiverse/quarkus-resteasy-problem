package io.quarkiverse.resteasy.problem.postprocessing;

import static io.quarkiverse.resteasy.problem.HttpProblemMother.badRequestProblem;
import static io.quarkiverse.resteasy.problem.postprocessing.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkiverse.resteasy.problem.HttpProblem;
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
