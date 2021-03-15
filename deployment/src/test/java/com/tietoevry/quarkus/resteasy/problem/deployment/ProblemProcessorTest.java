package com.tietoevry.quarkus.resteasy.problem.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProblemProcessorTest {

    ProblemProcessor problemProcessor = new ProblemProcessor();

    @Test
    void featureNameShouldBeValid() {
        assertThat(problemProcessor.createFeature().getName()).isEqualTo("resteasy-problem");
    }
}
