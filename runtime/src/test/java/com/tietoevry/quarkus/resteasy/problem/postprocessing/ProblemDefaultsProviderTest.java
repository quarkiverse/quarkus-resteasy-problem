package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import static com.tietoevry.quarkus.resteasy.problem.ProblemMother.badRequestProblem;
import static com.tietoevry.quarkus.resteasy.problem.ProblemMother.badRequestProblemBuilder;
import static com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

class ProblemDefaultsProviderTest {

    ProblemDefaultsProvider processor = new ProblemDefaultsProvider();

    @Test
    void shouldProvideDefaults() {
        ProblemContext context = simpleContext();

        Problem enhancedProblem = processor.apply(badRequestProblem(), context);

        assertThat(enhancedProblem.getInstance()).hasPath(context.uriInfo.getPath());
    }

    @Test
    void shouldNotOverrideExistingValues() {
        Problem originalProblem = badRequestProblemBuilder()
                .withType(URI.create("/business-error"))
                .withInstance(URI.create("/non-default-endpoint"))
                .build();

        Problem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem.getInstance()).hasPath("/non-default-endpoint");
    }

}
