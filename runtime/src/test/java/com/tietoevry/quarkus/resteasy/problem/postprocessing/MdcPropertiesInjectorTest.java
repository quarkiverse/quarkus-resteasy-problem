package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import static com.tietoevry.quarkus.resteasy.problem.ProblemMother.badRequestProblem;
import static com.tietoevry.quarkus.resteasy.problem.ProblemMother.badRequestProblemBuilder;
import static com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

class MdcPropertiesInjectorTest {

    static final String PROPERTY = "customProperty";

    MdcPropertiesInjector processor = new MdcPropertiesInjector(Sets.newHashSet("uuid", "another", PROPERTY));

    @BeforeEach
    void init() {
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldPreserveAllFields() {
        Problem enhancedProblem = processor.apply(badRequestProblem(), simpleContext());

        assertThat(enhancedProblem.getTitle()).isEqualTo("There's something wrong with your request");
        assertThat(enhancedProblem.getStatus()).isEqualTo(Status.BAD_REQUEST);
    }

    @Test
    void shouldAddOnlyExistingProperties() {
        MDC.put("uuid", "123");

        Problem enhancedProblem = processor.apply(badRequestProblem(), simpleContext());

        assertThat(enhancedProblem.getParameters())
                .containsEntry("uuid", "123")
                .doesNotContainKey("another");
    }

    @Test
    void shouldNotOverrideExistingProblemProperties() {
        String propertyValueFromProblem = "abc";
        MDC.put(PROPERTY, "123");
        Problem originalProblem = badRequestProblemBuilder()
                .with(PROPERTY, propertyValueFromProblem)
                .build();

        Problem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem.getParameters())
                .containsEntry(PROPERTY, propertyValueFromProblem);
    }

}
