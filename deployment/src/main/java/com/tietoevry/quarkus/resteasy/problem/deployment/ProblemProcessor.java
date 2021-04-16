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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.Priorities;
import org.jboss.logging.Logger;

public class ProblemProcessor {

    private static final String FEATURE_NAME = "resteasy-problem";
    private static final String PACKAGE = "com.tietoevry.quarkus.resteasy.problem.";

    private static final Logger logger = Logger.getLogger(FEATURE_NAME);

    private static Map<String, String> neededExceptionMappers() {
        Map<String, String> mappers = new LinkedHashMap<>();
        mappers.put("java.lang.Exception", "DefaultExceptionMapper");
        mappers.put("com.tietoevry.quarkus.resteasy.problem.HttpProblem", "HttpProblemMapper");
        mappers.put("javax.ws.rs.WebApplicationException", "jaxrs.WebApplicationExceptionMapper");
        mappers.put("javax.ws.rs.ForbiddenException", "jaxrs.JaxRsForbiddenExceptionMapper");
        mappers.put("javax.ws.rs.NotFoundException", "jaxrs.NotFoundExceptionMapper");
        mappers.put("io.quarkus.security.UnauthorizedException", "security.UnauthorizedExceptionMapper");
        mappers.put("io.quarkus.security.AuthenticationFailedException", "security.AuthenticationFailedExceptionMapper");
        mappers.put("io.quarkus.security.ForbiddenException", "security.ForbiddenExceptionMapper");
        mappers.put("javax.validation.ValidationException", "javax.ValidationExceptionMapper");
        mappers.put("javax.validation.ConstraintViolationException", "javax.ConstraintViolationExceptionMapper");
        mappers.put("org.zalando.problem.ThrowableProblem", "ZalandoProblemMapper");

        mappers.put("com.fasterxml.jackson.core.JsonProcessingException", "jackson.JsonProcessingExceptionMapper");
        mappers.put("javax.ws.rs.ProcessingException", "jsonb.RestEasyClassicJsonbExceptionMapper");
        mappers.put("javax.json.bind.JsonbException", "jsonb.JsonbExceptionMapper");

        return mappers.entrySet().stream()
                .filter(mapper -> {
                    String exceptionClass = mapper.getKey();
                    return classAvailable(exceptionClass);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static boolean classAvailable(String exceptionClass) {
        try {
            Class.forName(exceptionClass);
            return true;
        } catch (ClassNotFoundException e) {
            logger.debugf("%s not found in classpath, skipping", exceptionClass);
            return false;
        }
    }

    @BuildStep
    FeatureBuildItem createFeature() {
        return new FeatureBuildItem(FEATURE_NAME);
    }

    @BuildStep(onlyIf = RestEasyClassicDetector.class)
    void registerMappersForClassic(BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        neededExceptionMappers().forEach(
                (exceptionClass, mapperClass) -> providers
                        .produce(new ResteasyJaxrsProviderBuildItem(PACKAGE + mapperClass)));
    }

    @BuildStep(onlyIf = RestEasyReactiveDetector.class)
    void registerMappersForReactive(BuildProducer<ExceptionMapperBuildItem> providers) {
        neededExceptionMappers().forEach(
                (exceptionClass, mapperClass) -> providers.produce(
                        new ExceptionMapperBuildItem(PACKAGE + mapperClass, exceptionClass,
                                Priorities.AUTHENTICATION - 1, true)));
    }

    @BuildStep(onlyIf = JacksonDetector.class)
    void registerJacksonItems(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        additionalBeans.produce(new AdditionalBeanBuildItem(
                PACKAGE + "jackson.JacksonProblemModuleRegistrar"));
    }

    @BuildStep(onlyIf = JsonBDetector.class)
    void registerJsonbItems(BuildProducer<JsonbSerializerBuildItem> serializers) {
        serializers.produce(
                new JsonbSerializerBuildItem(PACKAGE + "jsonb.JsonbProblemSerializer"));
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

    @BuildStep(onlyIfNot = QuarkusSmallryeMetricsDetector.class)
    void warnOnMissingSmallryeMetricsDependency(ProblemBuildConfig config) {
        if (config.metricsEnabled) {
            logger.warn("quarkus.resteasy.problem.metrics.enabled is set to true, but quarkus-smallrye-metrics not "
                    + "found in the classpath");
        }
    }

}
