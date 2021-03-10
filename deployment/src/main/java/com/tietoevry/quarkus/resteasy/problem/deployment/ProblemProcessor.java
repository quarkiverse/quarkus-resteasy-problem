package com.tietoevry.quarkus.resteasy.problem.deployment;

import com.tietoevry.quarkus.resteasy.problem.DefaultExceptionMapper;
import com.tietoevry.quarkus.resteasy.problem.JacksonProblemModuleRegistrar;
import com.tietoevry.quarkus.resteasy.problem.JsonBProblemSerializer;
import com.tietoevry.quarkus.resteasy.problem.ProblemMapper;
import com.tietoevry.quarkus.resteasy.problem.ProblemRecorder;
import com.tietoevry.quarkus.resteasy.problem.javax.ConstraintViolationExceptionMapper;
import com.tietoevry.quarkus.resteasy.problem.javax.ValidationExceptionMapper;
import com.tietoevry.quarkus.resteasy.problem.javax.Violation;
import com.tietoevry.quarkus.resteasy.problem.jaxrs.JaxRsForbiddenExceptionMapper;
import com.tietoevry.quarkus.resteasy.problem.jaxrs.NotFoundExceptionMapper;
import com.tietoevry.quarkus.resteasy.problem.jaxrs.WebApplicationExceptionMapper;
import com.tietoevry.quarkus.resteasy.problem.misc.JsonProcessingExceptionMapper;
import com.tietoevry.quarkus.resteasy.problem.misc.JsonbExceptionMapper;
import com.tietoevry.quarkus.resteasy.problem.security.AuthenticationFailedExceptionMapper;
import com.tietoevry.quarkus.resteasy.problem.security.ForbiddenExceptionMapper;
import com.tietoevry.quarkus.resteasy.problem.security.UnauthorizedExceptionMapper;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.jsonb.spi.JsonbSerializerBuildItem;
import io.quarkus.resteasy.common.spi.ResteasyJaxrsProviderBuildItem;
import org.jboss.logging.Logger;

import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;
import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

public class ProblemProcessor {

    private static final String FEATURE_NAME = "resteasy-problem";
    private static final Logger logger = Logger.getLogger(FEATURE_NAME);

    private static final List<Class<? extends ExceptionMapper<?>>> COMMON_EXCEPTION_MAPPER_CLASSES = List.of(
            // TOP LEVEL
            DefaultExceptionMapper.class,
            ProblemMapper.class,

            // JAXRS
            WebApplicationExceptionMapper.class,
            JaxRsForbiddenExceptionMapper.class,
            NotFoundExceptionMapper.class,
            UnauthorizedExceptionMapper.class,
            AuthenticationFailedExceptionMapper.class,
            ForbiddenExceptionMapper.class,

            // JAVAX
            ValidationExceptionMapper.class,
            ConstraintViolationExceptionMapper.class
    );

    @BuildStep
    FeatureBuildItem createFeature() {
        return new FeatureBuildItem(FEATURE_NAME);
    }

    @BuildStep(onlyIf = JacksonDetector.class)
    void registerJacksonMapperCustomizer(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        additionalBeans.produce(new AdditionalBeanBuildItem(JacksonProblemModuleRegistrar.class));
    }

    @BuildStep(onlyIf = JsonBDetector.class)
    void registerJsonbItems(BuildProducer<JsonbSerializerBuildItem> serializers,
                                  BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        serializers.produce(new JsonbSerializerBuildItem(JsonBProblemSerializer.class.getName()));
        providers.produce(new ResteasyJaxrsProviderBuildItem(JsonbExceptionMapper.class.getName()));
    }

    @BuildStep
    void registerCommonProviders(BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        COMMON_EXCEPTION_MAPPER_CLASSES.stream()
                .map(Class::getName)
                .map(ResteasyJaxrsProviderBuildItem::new)
                .forEach(providers::produce);
    }

    @BuildStep(onlyIf = JacksonDetector.class)
    void registerJacksonProviders(BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
        providers.produce(new ResteasyJaxrsProviderBuildItem(JsonProcessingExceptionMapper.class.getName()));
    }

    @BuildStep
    ReflectiveClassBuildItem registerPojosForReflection() {
        return new ReflectiveClassBuildItem(true, true, Violation.class.getName());
    }

    @Record(STATIC_INIT)
    @BuildStep
    void resetRecorder(ProblemRecorder recorder, LiveReloadBuildItem liveReload) {
        if(liveReload.isLiveReload()) {
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
