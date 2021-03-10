package com.tietoevry.quarkus.resteasy.problem;

import com.tietoevry.quarkus.resteasy.problem.javax.Violation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class XmlProblemTest {

    @Test
    void jacksonConfigurationShouldBeValid() {
        String serialized = XmlProblem.serialize(exampleProblemBuilder().build());

        assertThat(serialized).isEqualTo(
                "<problem>"
                + "<status>400</status>"
                + "<title>Bad Request</title>"
                + "<custom_parameter>exampleValue</custom_parameter>"
                + "<singleton_list>test-element</singleton_list>"
                + "<violations>"
                + "<error>too long</error>"
                + "<field>first_field</field>"
                + "</violations>"
                + "<violations>"
                + "<error>too short</error>"
                + "<field>second_field</field>"
                + "</violations>"
                + "</problem>"
        );
    }

    private ProblemBuilder exampleProblemBuilder() {
        return Problem.builder()
                .withTitle(Status.BAD_REQUEST.getReasonPhrase())
                .withStatus(Status.BAD_REQUEST)
                .with("custom_parameter", "exampleValue")
                .with("singleton_list", List.of("test-element"))
                .with("violations", List.of(new Violation("too long", "first_field"), new Violation("too short", "second_field")));
    }

}
