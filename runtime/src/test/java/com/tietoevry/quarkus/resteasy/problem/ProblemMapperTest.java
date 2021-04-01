package com.tietoevry.quarkus.resteasy.problem;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

class ProblemMapperTest {

    ProblemMapper mapper = new ProblemMapper();

    @Test
    void responseShouldUseProblemStatus() {
        ThrowableProblem problem = ProblemMother.badRequestProblem();

        Response response = mapper.toResponse(problem);

        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void problemWithoutStatusShouldDefaultTo500() {
        ThrowableProblem exception = Problem.builder().build();

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(500);
    }

}
