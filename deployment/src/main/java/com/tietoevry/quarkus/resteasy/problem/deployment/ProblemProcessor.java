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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Priorities;
import org.jboss.logging.Logger;

public class ProblemProcessor {

    private static final String FEATURE_NAME = "resteasy-problem";
    private static final String EXTENSION_MAIN_PACKAGE = "com.tietoevry.quarkus.resteasy.problem.";

    private static final Logger logger = Logger.getLogger(FEATURE_NAME);

    /**
     * Don't change this to Capability.RESTEASY_JSON for the sake of older Quarkus versions
     */
    private static final String RESTEASY_JSON_CAPABILITY = "io.quarkus.resteasy.json";
    private static final String RESTEASY_JSON_LEGACY_CAPABILITY = "io.quarkus.resteasy-json";

    private static List<ExceptionMapperDefinition> neededExceptionMappers() {
        List<ExceptionMapperDefinition> mappers = new ArrayList<>();
        mappers.add(mapper("HttpProblemMapper").handling("com.tietoevry.quarkus.resteasy.problem.HttpProblem"));

        mappers.add(mapper("jaxrs.WebApplicationExceptionMapper").handling("javax.ws.rs.WebApplicationException"));
        mappers.add(mapper("jaxrs.JaxRsForbiddenExceptionMapper").handling("javax.ws.rs.ForbiddenException"));
        mappers.add(mapper("jaxrs.NotFoundExceptionMapper").handling("javax.ws.rs.NotFoundException"));
        mappers.add(mapper("jsonb.RestEasyClassicJsonbExceptionMapper").handling("javax.ws.rs.ProcessingException"));

        mappers.add(mapper("security.UnauthorizedExceptionMapper").handling("io.quarkus.security.UnauthorizedException"));
        mappers.add(mapper("security.AuthenticationFailedExceptionMapper")
                .handling("io.quarkus.security.AuthenticationFailedException"));
        mappers.add(mapper("security.AuthenticationRedirectExceptionMapper")
                .handling("io.quarkus.security.AuthenticationRedirectException"));
        mappers.add(mapper("security.AuthenticationCompletionExceptionMapper")
                .handling("io.quarkus.security.AuthenticationCompletionException"));
        mappers.add(mapper("security.ForbiddenExceptionMapper").handling("io.quarkus.security.ForbiddenException"));

        mappers.add(mapper("javax.ValidationExceptionMapper").handling("javax.validation.ValidationException"));
        mappers.add(
                mapper("javax.ConstraintViolationExceptionMapper").handling("javax.validation.ConstraintViolationException"));

        mappers.add(mapper("jackson.JsonProcessingExceptionMapper")
                .handling("com.fasterxml.jackson.core.JsonProcessingException").onlyIf(new JacksonDetector()));
        mappers.add(mapper("jackson.UnrecognizedPropertyExceptionMapper")
                .handling("com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException").onlyIf(new JacksonDetector()));
        mappers.add(mapper("jackson.InvalidFormatExceptionMapper")
                .handling("com.fasterxml.jackson.databind.exc.InvalidFormatException").onlyIf(new JacksonDetector()));

        mappers.add(mapper("jsonb.RestEasyClassicJsonbExceptionMapper").handling("javax.ws.rs.ProcessingException")
                .onlyIf(new JsonBDetector()));
        mappers.add(
                mapper("jsonb.JsonbExceptionMapper").handling("javax.json.bind.JsonbException").onlyIf(new JsonBDetector()));

        mappers.add(mapper("ZalandoProblemMapper").handling("org.zalando.problem.ThrowableProblem"));

        mappers.add(mapper("DefaultExceptionMapper").handling("java.lang.Exception"));

        return mappers.stream()
                .filter(mapper -> mapper.detector.getAsBoolean())
                .collect(Collectors.toList());
    }

    @BuildStep
    FeatureBuildItem createFeature(Capabilities capabilities) {
        if (!capabilities.isCapabilityPresent(RESTEASY_JSON_CAPABILITY)
                && !capabilities.isCapabilityPresent(RESTEASY_JSON_LEGACY_CAPABILITY)) {
            logger.error("This extension is useless without RESTeasy Json Provider. Please add "
                    + "`quarkus-resteasy-jackson` or `quarkus-resteasy-jsonb` to your pom.xml.");
        }
        return new FeatureBuildItem(FEATURE_NAME);
    }

    @BuildStep(onlyIf = RestEasyClassicDetector.class)
    void registerMappersForClassic(BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        neededExceptionMappers().forEach(mapper -> providers.produce(
                new ResteasyJaxrsProviderBuildItem(EXTENSION_MAIN_PACKAGE + mapper.mapperClassName)));
    }

    @BuildStep(onlyIf = RestEasyReactiveDetector.class)
    void registerMappersForReactive(BuildProducer<ExceptionMapperBuildItem> providers) {
        neededExceptionMappers().forEach(mapper -> providers.produce(
                new ExceptionMapperBuildItem(EXTENSION_MAIN_PACKAGE + mapper.mapperClassName,
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
        return new ReflectiveClassBuildItem(true, true, EXTENSION_MAIN_PACKAGE + "javax.Violation");
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

    @BuildStep(onlyIfNot = QuarkusSmallryeMetricsDetector.class)
    void warnOnMissingSmallryeMetricsDependency(ProblemBuildConfig config) {
        if (config.metricsEnabled) {
            logger.warn("quarkus.resteasy.problem.metrics.enabled is set to true, but quarkus-smallrye-metrics not "
                    + "found in the classpath");
        }
    }

}
