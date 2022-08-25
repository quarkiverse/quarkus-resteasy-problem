package com.tietoevry.quarkus.resteasy.problem.deployment;

import static com.tietoevry.quarkus.resteasy.problem.deployment.ExceptionMapperDefinition.mapper;
import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;
import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

import com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemRecorder;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.jsonb.spi.JsonbSerializerBuildItem;
import io.quarkus.resteasy.common.spi.ResteasyJaxrsProviderBuildItem;
import io.quarkus.resteasy.reactive.spi.ExceptionMapperBuildItem;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.Priorities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProblemProcessor {

    private static final String FEATURE_NAME = "resteasy-problem";
    private static final String EXTENSION_MAIN_PACKAGE = "com.tietoevry.quarkus.resteasy.problem.";

    /**
     * Don't change this to constants from Capability for the sake of older Quarkus versions
     */
    private static final List<String> RESTEASY_JSON_CAPABILITIES = Arrays.asList(
            "io.quarkus.resteasy.json",
            "io.quarkus.resteasy-json",
            "io.quarkus.jsonb",
            "io.quarkus.jackson");

    private static List<ExceptionMapperDefinition> neededExceptionMappers() {
        Stream<ExceptionMapperDefinition> allMappers = Stream.of(
                mapper(EXTENSION_MAIN_PACKAGE + "HttpProblemMapper")
                        .thatHandles("com.tietoevry.quarkus.resteasy.problem.HttpProblem"),

                mapper(EXTENSION_MAIN_PACKAGE + "jaxrs.WebApplicationExceptionMapper")
                        .thatHandles("javax.ws.rs.WebApplicationException"),
                mapper(EXTENSION_MAIN_PACKAGE + "jaxrs.JaxRsForbiddenExceptionMapper")
                        .thatHandles("javax.ws.rs.ForbiddenException"),
                mapper(EXTENSION_MAIN_PACKAGE + "jaxrs.NotFoundExceptionMapper").thatHandles("javax.ws.rs.NotFoundException"),
                mapper(EXTENSION_MAIN_PACKAGE + "jsonb.RestEasyClassicJsonbExceptionMapper")
                        .thatHandles("javax.ws.rs.ProcessingException"),

                mapper(EXTENSION_MAIN_PACKAGE + "security.UnauthorizedExceptionMapper")
                        .thatHandles("io.quarkus.security.UnauthorizedException"),
                mapper(EXTENSION_MAIN_PACKAGE + "security.AuthenticationFailedExceptionMapper")
                        .thatHandles("io.quarkus.security.AuthenticationFailedException"),
                mapper(EXTENSION_MAIN_PACKAGE + "security.AuthenticationRedirectExceptionMapper")
                        .thatHandles("io.quarkus.security.AuthenticationRedirectException"),
                mapper(EXTENSION_MAIN_PACKAGE + "security.AuthenticationCompletionExceptionMapper")
                        .thatHandles("io.quarkus.security.AuthenticationCompletionException"),
                mapper(EXTENSION_MAIN_PACKAGE + "security.ForbiddenExceptionMapper")
                        .thatHandles("io.quarkus.security.ForbiddenException"),

                mapper(EXTENSION_MAIN_PACKAGE + "validation.ValidationExceptionMapper")
                        .thatHandles("javax.validation.ValidationException"),

                mapper(EXTENSION_MAIN_PACKAGE + "validation.ConstraintViolationExceptionMapper")
                        .thatHandles("javax.validation.ConstraintViolationException"),

                mapper(EXTENSION_MAIN_PACKAGE + "jackson.JsonProcessingExceptionMapper")
                        .thatHandles("com.fasterxml.jackson.core.JsonProcessingException").onlyIf(new JacksonDetector()),
                mapper(EXTENSION_MAIN_PACKAGE + "jackson.UnrecognizedPropertyExceptionMapper")
                        .thatHandles("com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException")
                        .onlyIf(new JacksonDetector()),
                mapper(EXTENSION_MAIN_PACKAGE + "jackson.InvalidFormatExceptionMapper")
                        .thatHandles("com.fasterxml.jackson.databind.exc.InvalidFormatException").onlyIf(new JacksonDetector()),

                mapper(EXTENSION_MAIN_PACKAGE + "jsonb.RestEasyClassicJsonbExceptionMapper")
                        .thatHandles("javax.ws.rs.ProcessingException")
                        .onlyIf(new JsonBDetector()),

                mapper(EXTENSION_MAIN_PACKAGE + "jsonb.JsonbExceptionMapper").thatHandles("javax.json.bind.JsonbException")
                        .onlyIf(new JsonBDetector()),

                mapper(EXTENSION_MAIN_PACKAGE + "ZalandoProblemMapper").thatHandles("org.zalando.problem.ThrowableProblem"),

                mapper(EXTENSION_MAIN_PACKAGE + "DefaultExceptionMapper").thatHandles("java.lang.Exception"));

        return allMappers
                .filter(ExceptionMapperDefinition::isNeeded)
                .collect(Collectors.toList());
    }

    @BuildStep
    FeatureBuildItem createFeature(Capabilities capabilities) {
        if (RESTEASY_JSON_CAPABILITIES.stream().noneMatch(capabilities::isCapabilityPresent)) {
            logger().error("`quarkus-resteasy-problem` extension is useless without RESTeasy Json Provider. Please add "
                    + "`quarkus-resteasy-jackson` or `quarkus-resteasy-jsonb` (or reactive versions) to your pom.xml.");
        }
        return new FeatureBuildItem(FEATURE_NAME);
    }

    @BuildStep(onlyIf = RestEasyClassicDetector.class)
    void registerMappersForClassic(BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        neededExceptionMappers().forEach(mapper -> providers.produce(
                new ResteasyJaxrsProviderBuildItem(mapper.mapperClassName)));
    }

    @BuildStep(onlyIf = RestEasyReactiveDetector.class)
    void registerMappersForReactive(BuildProducer<ExceptionMapperBuildItem> providers) {
        neededExceptionMappers().forEach(mapper -> providers.produce(
                new ExceptionMapperBuildItem(mapper.mapperClassName,
                        mapper.exceptionClassName, Priorities.AUTHENTICATION - 1, true)));
    }

    @BuildStep(onlyIf = JacksonDetector.class)
    void registerJacksonItems(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        additionalBeans.produce(new AdditionalBeanBuildItem(
                EXTENSION_MAIN_PACKAGE + "jackson.JacksonProblemModuleRegistrar"));
    }

    @BuildStep(onlyIf = JsonBDetector.class)
    void registerJsonbItems(BuildProducer<JsonbSerializerBuildItem> serializers) {
        serializers.produce(
                new JsonbSerializerBuildItem(EXTENSION_MAIN_PACKAGE + "jsonb.JsonbProblemSerializer"));
    }

    @BuildStep
    ReflectiveClassBuildItem registerPojosForReflection() {
        return new ReflectiveClassBuildItem(true, true, EXTENSION_MAIN_PACKAGE + "validation.Violation");
    }

    @Record(STATIC_INIT)
    @BuildStep
    void resetRecorder(ProblemRecorder recorder, LiveReloadBuildItem liveReload) {
        if (liveReload.isLiveReload()) {
            recorder.reset();
        }
    }

    @Record(RUNTIME_INIT)
    @BuildStep
    void setupMdc(ProblemRecorder recorder, ProblemBuildConfig config) {
        recorder.configureMdc(config.includeMdcProperties);
    }

    @Record(RUNTIME_INIT)
    @BuildStep(onlyIf = QuarkusSmallryeMetricsDetector.class)
    void setupMetrics(ProblemRecorder recorder, ProblemBuildConfig config) {
        if (config.metricsEnabled) {
            recorder.enableMetrics();
        }
    }

    protected Logger logger() {
        return LoggerFactory.getLogger(FEATURE_NAME);
    }
}
