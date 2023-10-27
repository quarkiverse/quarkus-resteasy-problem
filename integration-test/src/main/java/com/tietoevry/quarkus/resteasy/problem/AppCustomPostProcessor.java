package com.tietoevry.quarkus.resteasy.problem;

import com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContext;
import com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemPostProcessor;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Validator;

@ApplicationScoped
@Startup
class AppCustomPostProcessor implements ProblemPostProcessor {

    @Inject
    Validator validator;

    @Override
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        return HttpProblem.builder(problem)
                .with("injected_from_custom_post_processor", "you called " + context.path)
                .build();
    }
}
