package com.tietoevry.quarkus.resteasy.problem;

import java.util.Set;
import org.slf4j.MDC;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

class MdcPropertiesProcessor implements ProblemProcessor {

    private final Set<String> properties;

    public MdcPropertiesProcessor(Set<String> properties) {
        this.properties = properties;
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public Problem apply(Problem problem, Throwable cause) {
        ProblemBuilder builder = Problem.builder()
                .withType(problem.getType())
                .withTitle(problem.getTitle())
                .withStatus(problem.getStatus())
                .withInstance(problem.getInstance())
                .withDetail(problem.getDetail());

        properties.forEach(field -> {
            String propertyValue = MDC.get(field);
            if (propertyValue != null) {
                builder.with(field, propertyValue);
            }
        });

        problem.getParameters().forEach(builder::with);
        return builder.build();
    }

}
