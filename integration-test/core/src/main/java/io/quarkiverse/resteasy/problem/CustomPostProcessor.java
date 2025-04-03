package io.quarkiverse.resteasy.problem;

import io.quarkiverse.resteasy.problem.postprocessing.ProblemContext;
import io.quarkiverse.resteasy.problem.postprocessing.ProblemPostProcessor;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class CustomPostProcessor implements ProblemPostProcessor {

    @Override
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        return HttpProblem.builder(problem)
                .with("injected_from_custom_post_processor", "you called " + context.path)
                .build();
    }
}
