package com.tietoevry.quarkus.resteasy.problem;

import static com.tietoevry.quarkus.resteasy.problem.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;

class MdcPropertiesProcessorTest {

    MdcPropertiesProcessor processor = new MdcPropertiesProcessor(Sets.newHashSet("uuid", "another"));

    @BeforeEach
    void init() {
        MDC.clear();
    }

    @Test
    void processorShouldPreserveAllFields() {
        Problem originalProblem = exampleProblemBuilder().build();

        Problem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem.getTitle()).isEqualTo("title");
        assertThat(enhancedProblem.getStatus()).isEqualTo(Status.BAD_REQUEST);
    }

    @Test
    void applyShouldAddOnlyExistingProperties() {
        MDC.put("uuid", "123");
        Problem originalProblem = exampleProblemBuilder().build();

        Problem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem.getParameters()).containsEntry("uuid", "123");
        assertThat(enhancedProblem.getParameters()).doesNotContainKey("another");
    }

    @Test
    void mdcPropertyShouldNotOverrideExisting() {
        MDC.put("customProperty", "123");
        Problem originalProblem = exampleProblemBuilder()
                .with("customProperty", "abc")
                .build();

        Problem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem.getParameters()).containsEntry("customProperty", "abc");
    }

    private ProblemBuilder exampleProblemBuilder() {
        return Problem.builder()
                .withTitle("title")
                .withStatus(Status.BAD_REQUEST);
    }

}
