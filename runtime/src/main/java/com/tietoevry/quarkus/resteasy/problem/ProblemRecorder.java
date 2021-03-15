package com.tietoevry.quarkus.resteasy.problem;

import io.quarkus.runtime.annotations.Recorder;
import java.util.Set;

@Recorder
public class ProblemRecorder {

    public void reset() {
        ExceptionMapperBase.resetProcessors();
    }

    public void configureMdc(Set<String> includeMdcProperties) {
        if (!includeMdcProperties.isEmpty()) {
            ExceptionMapperBase.registerProcessor(new MdcPropertiesProcessor(includeMdcProperties));
        }
    }

    public void enableMetrics() {
        ExceptionMapperBase.registerProcessor(new HttpErrorMetricsProcessor());
    }
}
