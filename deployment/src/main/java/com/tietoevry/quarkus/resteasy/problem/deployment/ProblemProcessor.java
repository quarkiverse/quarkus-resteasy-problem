package com.tietoevry.quarkus.resteasy.problem.deployment;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;
import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

import com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemRecorder;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.jsonb.spi.JsonbSerializerBuildItem;
import io.quarkus.resteasy.common.spi.ResteasyJaxrsProviderBuildItem;
import io.quarkus.resteasy.reactive.spi.ExceptionMapperBuildItem;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.Priorities;
import org.jboss.logging.Logger;

public class ProblemProcessor {

    private static final String FEATURE_NAME = "resteasy-problem";
    private static final String PACKAGE = "com.tietoevry.quarkus.resteasy.problem.";

    private static final Map<String, String> DEFAULT_MAPPERS = defaultMappers();

    private static Map<String, String> defaultMappers() {
        Map<String, String> mappers = new LinkedHashMap<>();
        mappers.put("java.lang.Exception", "DefaultExceptionMapper");
        mappers.put(PACKAGE + "HttpProblem", "HttpProblemMapper");
        mappers.put("javax.ws.rs.WebApplicationException", "jaxrs.WebApplicationExceptionMapper");
        mappers.put("javax.ws.rs.ForbiddenException", "jaxrs.JaxRsForbiddenExceptionMapper");
        mappers.put("javax.ws.rs.NotFoundException", "jaxrs.NotFoundExceptionMapper");
        mappers.put("io.quarkus.security.UnauthorizedException", "security.UnauthorizedExceptionMapper");
        mappers.put("io.quarkus.security.AuthenticationFailedException", "security.AuthenticationFailedExceptionMapper");
        mappers.put("io.quarkus.security.ForbiddenException", "security.ForbiddenExceptionMapper");
        return Collections.unmodifiableMap(mappers);
    }

    private static final Map<String, String> VALIDATION_MAPPERS = validationMappers();

    private static Map<String, String> validationMappers() {
        Map<String, String> mappers = new LinkedHashMap<>();
        mappers.put("javax.validation.ValidationException", "javax.ValidationExceptionMapper");
        mappers.put("javax.validation.ConstraintViolationException", "javax.ConstraintViolationExceptionMapper");
        return Collections.unmodifiableMap(mappers);
    }

    private static final Logger logger = Logger.getLogger(FEATURE_NAME);

    @BuildStep
    FeatureBuildItem createFeature() {
        return new FeatureBuildItem(FEATURE_NAME);
    }

    @BuildStep(onlyIf = RestEasyClassicDetector.class)
    void registerDefaultMappersForClassic(BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        DEFAULT_MAPPERS.forEach(
                (exceptionClass, mapperClass) -> providers
                        .produce(new ResteasyJaxrsProviderBuildItem(PACKAGE + mapperClass)));
    }

    @BuildStep(onlyIf = RestEasyReactiveDetector.class)
    void registerDefaultMappersForReactive(BuildProducer<ExceptionMapperBuildItem> providers) {
        DEFAULT_MAPPERS.forEach(
                (exceptionClass, mapperClass) -> providers.produce(
                        new ExceptionMapperBuildItem(PACKAGE + mapperClass, exceptionClass,
                                Priorities.AUTHENTICATION - 1, true)));
    }

    @BuildStep(onlyIf = BeanValidationApiDetector.class)
    void registerValidationMappersForClassic(BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        VALIDATION_MAPPERS.forEach(
                (exceptionClass, mapperClass) -> providers
                        .produce(new ResteasyJaxrsProviderBuildItem(PACKAGE + mapperClass)));
    }

    @BuildStep(onlyIf = BeanValidationApiDetector.class)
    void registerValidationMappersForReactive(BuildProducer<ExceptionMapperBuildItem> providers) {
        VALIDATION_MAPPERS.forEach(
                (exceptionClass, mapperClass) -> providers.produce(
                        new ExceptionMapperBuildItem(PACKAGE + mapperClass, exceptionClass,
                                Priorities.AUTHENTICATION - 1, true)));
    }

    @BuildStep(onlyIf = JacksonDetector.class)
    void registerJacksonItems(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        additionalBeans.produce(new AdditionalBeanBuildItem(
                PACKAGE + "jackson.JacksonProblemModuleRegistrar"));
    }

    @BuildStep(onlyIf = { JacksonDetector.class, RestEasyClassicDetector.class })
    void registerJacksonItemsClassic(BuildProducer<ResteasyJaxrsProviderBuildItem> classicProviders) {
        classicProviders.produce(new ResteasyJaxrsProviderBuildItem(
                PACKAGE + "jackson.JsonProcessingExceptionMapper"));
    }

    @BuildStep(onlyIf = { JacksonDetector.class, RestEasyReactiveDetector.class })
    void registerJacksonItemsForReactive(BuildProducer<ExceptionMapperBuildItem> reactiveProviders) {
        reactiveProviders.produce(new ExceptionMapperBuildItem(
                PACKAGE + "jackson.JsonProcessingExceptionMapper",
                "com.fasterxml.jackson.core.JsonProcessingException", Priorities.USER, true));
    }

    @BuildStep(onlyIf = JsonBDetector.class)
    void registerJsonbItems(BuildProducer<JsonbSerializerBuildItem> serializers) {
        serializers.produce(
                new JsonbSerializerBuildItem(PACKAGE + "jsonb.JsonbProblemSerializer"));
    }

    @BuildStep(onlyIf = { JsonBDetector.class, RestEasyClassicDetector.class })
    void registerJsonbItemsForClassic(BuildProducer<ResteasyJaxrsProviderBuildItem> classicProviders) {
        classicProviders.produce(
                new ResteasyJaxrsProviderBuildItem(PACKAGE + "jsonb.RestEasyClassicJsonbExceptionMapper"));
    }

    @BuildStep(onlyIf = { JsonBDetector.class, RestEasyReactiveDetector.class })
    void registerJsonbItemsReactive(BuildProducer<ExceptionMapperBuildItem> reactiveProviders) {
        reactiveProviders.produce(new ExceptionMapperBuildItem(
                PACKAGE + "jsonb.RestEasyClassicJsonbExceptionMapper", "javax.ws.rs.ProcessingException",
                Priorities.USER, true));

        reactiveProviders.produce(new ExceptionMapperBuildItem(
                PACKAGE + "jsonb.JsonbExceptionMapper", "javax.json.bind.JsonbException",
                Priorities.USER, true));

    }

    @BuildStep(onlyIf = { ZalandoProblemDetector.class, RestEasyClassicDetector.class })
    void registerZalandoProblemMapperClassic(BuildProducer<ResteasyJaxrsProviderBuildItem> classicProviders) {
        classicProviders
                .produce(new ResteasyJaxrsProviderBuildItem(PACKAGE + "ZalandoProblemMapper"));
    }

    @BuildStep(onlyIf = { ZalandoProblemDetector.class, RestEasyReactiveDetector.class })
    void registerZalandoProblemMapperForReactive(BuildProducer<ExceptionMapperBuildItem> reactiveProviders) {
        reactiveProviders.produce(new ExceptionMapperBuildItem(
                PACKAGE + "ZalandoProblemMapper", "org.zalando.problem.ThrowableProblem",
                Priorities.USER, true));
    }

    @BuildStep
    ReflectiveClassBuildItem registerPojosForReflection() {
        return new ReflectiveClassBuildItem(true, true, PACKAGE + "javax.Violation");
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

    @Record(STATIC_INIT)
    @BuildStep(onlyIfNot = QuarkusSmallryeMetricsDetector.class)
    void warnOnMissingSmallryeMetricsDependency(ProblemRecorder recorder, ProblemBuildConfig config) {
        if (config.metricsEnabled) {
            logger.warn("quarkus.resteasy.problem.metrics.enabled is set to true, but quarkus-smallrye-metrics not "
                    + "found in the classpath");
        }
    }

}
