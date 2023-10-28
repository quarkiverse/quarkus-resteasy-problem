package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import static com.tietoevry.quarkus.resteasy.problem.HttpProblemMother.badRequestProblem;
import static com.tietoevry.quarkus.resteasy.problem.HttpProblemMother.badRequestProblemBuilder;
import static com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContextMother.simpleContext;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Sets;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

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
        HttpProblem enhancedProblem = processor.apply(badRequestProblem(), simpleContext());

        assertThat(enhancedProblem.getTitle()).isEqualTo("There's something wrong with your request");
        assertThat(enhancedProblem.getStatusCode()).isEqualTo(BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldAddOnlyExistingProperties() {
        MDC.put("uuid", "123");

        HttpProblem enhancedProblem = processor.apply(badRequestProblem(), simpleContext());

        assertThat(enhancedProblem.getParameters())
                .containsEntry("uuid", "123")
                .doesNotContainKey("another");
    }

    @Test
    void shouldNotOverrideExistingProblemProperties() {
        String propertyValueFromProblem = "abc";
        MDC.put(PROPERTY, "123");
        HttpProblem originalProblem = badRequestProblemBuilder()
                .with(PROPERTY, propertyValueFromProblem)
                .build();

        HttpProblem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem.getParameters())
                .containsEntry(PROPERTY, propertyValueFromProblem);
    }

}
