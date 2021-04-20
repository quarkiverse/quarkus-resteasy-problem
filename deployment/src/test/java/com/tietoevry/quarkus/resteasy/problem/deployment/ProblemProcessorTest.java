package com.tietoevry.quarkus.resteasy.problem.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.deployment.Capabilities;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

class ProblemProcessorTest {

    ProblemProcessor problemProcessor = new ProblemProcessor();

    @Test
    void featureNameShouldBeValid() {
        assertThat(problemProcessor.createFeature(new Capabilities(new HashSet<>())).getName())
                .isEqualTo("resteasy-problem");
    }
}
