package com.tietoevry.quarkus.resteasy.problem.deployment;

import java.util.function.BooleanSupplier;

class ExceptionMapperDefinition {

    static MapperDefinitionFor mapper(String mapper) {
        return new MapperDefinitionFor(mapper);
    }

    static class MapperDefinitionFor {
        private final String mapper;

        private MapperDefinitionFor(String mapper) {
            this.mapper = mapper;
        }

        ExceptionMapperDefinition handling(String exception) {
            return new ExceptionMapperDefinition(exception, mapper, new ClasspathDetector(exception));
        }
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
