package com.tietoevry.quarkus.resteasy.problem;

import java.net.URI;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

public class ProblemMother {

    public static final String SERIALIZED_BAD_REQUEST_PROBLEM = "{\"status\":400,\"title\":\"Something wrong in the dirt\"}";
    public static final String SERIALIZED_COMPLEX_PROBLEM = "{\"type\":\"URI:goeshere\",\"status\":400,\"title\":\"Something wrong in the dirt\",\"detail\":\"Deep down wrongness, zażółć gęślą jaźń for Håkensth\",\"custom_field_1\":\"too long\",\"custom_field_2\":\"too short\"}";

    public static ThrowableProblem badRequestProblem() {
        return badRequestProblemBuilder().build();
    }

    public static ProblemBuilder badRequestProblemBuilder() {
        return Problem.builder()
                .withTitle("There's something wrong with your request")
                .withStatus(Status.BAD_REQUEST);
    }

    public static ProblemBuilder complexProblem() {
        return Problem.builder()
                .withType(URI.create("URI:goeshere"))
                .withStatus(Status.BAD_REQUEST)
                .withTitle("Something wrong in the dirt")
                .withDetail("Deep down wrongness, zażółć gęślą jaźń for Håkensth")
                .with("custom_field_1", "too long")
                .with("custom_field_2", "too short");
    }
}
