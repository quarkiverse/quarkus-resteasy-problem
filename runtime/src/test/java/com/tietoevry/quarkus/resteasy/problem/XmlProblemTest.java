package com.tietoevry.quarkus.resteasy.problem;

import com.tietoevry.quarkus.resteasy.problem.javax.Violation;
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
                "<problem xmlns=\"urn:ietf:rfc:7807\">\n"
                + "  <status xmlns=\"\">400</status>\n"
                + "  <title xmlns=\"\">Bad Request</title>\n"
                + "  <custom_parameter xmlns=\"\">exampleValue</custom_parameter>\n"
                + "  <singleton_list xmlns=\"\">test-element</singleton_list>\n"
                + "  <violations xmlns=\"\">\n"
                + "    <error>too long</error>\n"
                + "    <field>first_field</field>\n"
                + "  </violations>\n"
                + "  <violations xmlns=\"\">\n"
                + "    <error>too short</error>\n"
                + "    <field>second_field</field>\n"
                + "  </violations>\n"
                + "</problem>\n"
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
