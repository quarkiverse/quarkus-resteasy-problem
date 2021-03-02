package com.tietoevry.quarkus.resteasy.problem.deployment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProblemProcessorTest {

    ProblemProcessor problemProcessor = new ProblemProcessor();

    @Test
    void featureNameShouldBeValid() {
        assertThat(problemProcessor.createFeature().getName()).isEqualTo("resteasy-problem");
    }
}
