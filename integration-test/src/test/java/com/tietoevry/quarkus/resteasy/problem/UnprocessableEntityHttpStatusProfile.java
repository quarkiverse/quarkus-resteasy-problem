package com.tietoevry.quarkus.resteasy.problem;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Collections;
import java.util.Map;

public class UnprocessableEntityHttpStatusProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Collections.singletonMap("resteasy.problem.constraint-violation.http.status", "422");
    }
}
