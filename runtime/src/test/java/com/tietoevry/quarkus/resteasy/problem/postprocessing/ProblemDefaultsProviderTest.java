package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import static com.tietoevry.quarkus.resteasy.problem.HttpProblemMother.badRequestProblem;
import static com.tietoevry.quarkus.resteasy.problem.HttpProblemMother.badRequestProblemBuilder;
import static com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.net.URI;
import org.junit.jupiter.api.Test;

class ProblemDefaultsProviderTest {

    ProblemDefaultsProvider processor = new ProblemDefaultsProvider();

    @Test
    void shouldProvideDefaults() {
        ProblemContext context = simpleContext();

        HttpProblem enhancedProblem = processor.apply(badRequestProblem(), context);

        assertThat(enhancedProblem.getInstance()).hasPath(context.uriInfo.getPath());
    }

    @Test
    void shouldNotOverrideExistingValues() {
        HttpProblem originalProblem = badRequestProblemBuilder()
                .withType(URI.create("/business-error"))
                .withInstance(URI.create("/non-default-endpoint"))
                .build();

        HttpProblem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem.getInstance()).hasPath("/non-default-endpoint");
    }

}
