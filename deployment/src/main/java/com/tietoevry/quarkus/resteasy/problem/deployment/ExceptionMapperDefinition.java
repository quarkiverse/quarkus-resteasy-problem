package com.tietoevry.quarkus.resteasy.problem.deployment;

import java.util.function.BooleanSupplier;

final class ExceptionMapperDefinition {

    static ExceptionClassSupplier mapper(String mapper) {
        return exception -> new ExceptionMapperDefinition(exception, mapper, new ClasspathDetector(exception));
    }

    interface ExceptionClassSupplier {
        ExceptionMapperDefinition handling(String exception);
    }

    final String exceptionClassName;
    final String mapperClassName;
    final BooleanSupplier detector;

    private ExceptionMapperDefinition(String exceptionClassName, String mapperClassName, BooleanSupplier detector) {
        this.exceptionClassName = exceptionClassName;
        this.mapperClassName = mapperClassName;
        this.detector = detector;
    }

    ExceptionMapperDefinition onlyIf(BooleanSupplier detector) {
        return new ExceptionMapperDefinition(exceptionClassName, mapperClassName, detector);
    }
}
