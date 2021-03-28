package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import java.util.Set;
import org.slf4j.MDC;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

/**
 * Injects existing MDC properties listed in the configuration into final response. Missing MDC values and properties already
 * defined in Problem instance are skipped.
 */
class MdcPropertiesInjector implements ProblemPostProcessor {

    private final Set<String> properties;

    public MdcPropertiesInjector(Set<String> properties) {
        this.properties = properties;
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public ProblemBuilder apply(ProblemBuilder builder, ProblemContext context) {
        if (properties.isEmpty()) {
            return builder;
        }

        Problem problem = builder.build();

        properties.stream()
                .filter(propertyName -> !problem.getParameters().containsKey(propertyName))
                .filter(propertyName -> MDC.get(propertyName) != null)
                .forEach(propertyName -> builder.with(propertyName, MDC.get(propertyName)));

        return builder;
    }

}
