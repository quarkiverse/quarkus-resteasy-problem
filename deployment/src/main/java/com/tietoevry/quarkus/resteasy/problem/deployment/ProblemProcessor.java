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
import java.util.Arrays;
import java.util.List;
import org.jboss.logging.Logger;

public class ProblemProcessor {

    private static final String FEATURE_NAME = "resteasy-problem";
    private static final Logger logger = Logger.getLogger(FEATURE_NAME);

    private static final List<String> EXCEPTION_MAPPER_CLASSES = Arrays.asList(
            "com.tietoevry.quarkus.resteasy.problem.DefaultExceptionMapper",
            "com.tietoevry.quarkus.resteasy.problem.ProblemMapper",

            // JAXRS
            "com.tietoevry.quarkus.resteasy.problem.jaxrs.WebApplicationExceptionMapper",
            "com.tietoevry.quarkus.resteasy.problem.jaxrs.JaxRsForbiddenExceptionMapper",
            "com.tietoevry.quarkus.resteasy.problem.jaxrs.NotFoundExceptionMapper",

            // SECURITY
            "com.tietoevry.quarkus.resteasy.problem.security.UnauthorizedExceptionMapper",
            "com.tietoevry.quarkus.resteasy.problem.security.AuthenticationFailedExceptionMapper",
            "com.tietoevry.quarkus.resteasy.problem.security.ForbiddenExceptionMapper",

            // JAVAX
            "com.tietoevry.quarkus.resteasy.problem.javax.ValidationExceptionMapper",
            "com.tietoevry.quarkus.resteasy.problem.javax.ConstraintViolationExceptionMapper");

    @BuildStep
    FeatureBuildItem createFeature() {
        return new FeatureBuildItem(FEATURE_NAME);
    }

    @BuildStep(onlyIf = JacksonDetector.class)
    void registerJacksonItems(BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        additionalBeans.produce(new AdditionalBeanBuildItem(
                "com.tietoevry.quarkus.resteasy.problem.jackson.JacksonProblemModuleRegistrar"));

        providers.produce(new ResteasyJaxrsProviderBuildItem(
                "com.tietoevry.quarkus.resteasy.problem.jackson.JsonProcessingExceptionMapper"));
    }

    @BuildStep(onlyIf = JsonBDetector.class)
    void registerJsonbItems(BuildProducer<JsonbSerializerBuildItem> serializers,
            BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        serializers.produce(
                new JsonbSerializerBuildItem("com.tietoevry.quarkus.resteasy.problem.jsonb.JsonbProblemSerializer"));
        providers.produce(
                new ResteasyJaxrsProviderBuildItem("com.tietoevry.quarkus.resteasy.problem.jsonb.JsonbExceptionMapper"));
    }

    @BuildStep
    void registerCommonProviders(BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        EXCEPTION_MAPPER_CLASSES.stream()
                .map(ResteasyJaxrsProviderBuildItem::new)
                .forEach(providers::produce);
    }

    @BuildStep
    ReflectiveClassBuildItem registerPojosForReflection() {
        return new ReflectiveClassBuildItem(true, true,
                "com.tietoevry.quarkus.resteasy.problem.javax.Violation");
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
