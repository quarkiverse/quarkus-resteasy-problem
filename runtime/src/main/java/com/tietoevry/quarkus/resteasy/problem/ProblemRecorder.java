package com.tietoevry.quarkus.resteasy.problem;

import io.quarkus.runtime.annotations.Recorder;
import java.util.Set;

@Recorder
public class ProblemRecorder {

    public void reset() {
        ExceptionMapperBase.postProcessorsRegistry.reset();
    }

    public void configureMdc(Set<String> includeMdcProperties) {
        if (!includeMdcProperties.isEmpty()) {
            ExceptionMapperBase.postProcessorsRegistry.register(new MdcPropertiesProcessor(includeMdcProperties));
        }
    }

    public void enableMetrics() {
        ExceptionMapperBase.postProcessorsRegistry.register(new HttpErrorMetricsProcessor());
    }

}
