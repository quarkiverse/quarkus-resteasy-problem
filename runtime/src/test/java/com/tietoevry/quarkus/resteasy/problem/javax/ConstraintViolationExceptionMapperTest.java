package com.tietoevry.quarkus.resteasy.problem.javax;

import static com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase.APPLICATION_PROBLEM_JSON;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.ws.rs.core.Response;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.Test;

class ConstraintViolationExceptionMapperTest {

    static final Validator VALIDATOR = Validation.byProvider(HibernateValidator.class)
            .configure()
            .buildValidatorFactory()
            .getValidator();

    ConstraintViolationExceptionMapper mapper = new ConstraintViolationExceptionMapper();

    @Test
    void shouldIncludeAllViolations() {
        ConstraintViolationException exception = createValidationException();

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_PROBLEM_JSON);
        ConstraintViolationProblem problem = (ConstraintViolationProblem) response.getEntity();
        List<Violation> violations = (List<Violation>) problem.getParameters().get("violations");
        assertThat(violations)
                .hasSize(2)
                .usingFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        new Violation("must be greater than or equal to 20", "numericField"),
                        new Violation("length must be between 10 and 30", "stringField"));
    }

    private ConstraintViolationException createValidationException() {
        return new ConstraintViolationException(VALIDATOR.validate(new EndpointPayloadWrapper()));
    }

    /**
     * This strange hierarchy for this test double is because Hibernate + JaxRs generate pretty complex path for
     * violation subjects (arguments, payloads) paths, i.e: throwConstraintViolationException.arg0.numericField,
     * but we want only the last part of the path in our responses
     */
    static class EndpointPayloadWrapper {

        @Valid
        final Wrapper throwConstraintViolationException = new Wrapper();

        static class Wrapper {
            @Valid
            final InvalidPayload arg0 = new InvalidPayload();

            static class InvalidPayload {

                @Min(20)
                final int numericField = 10;

                @Length(min = 10, max = 30)
                final String stringField = "";
            }
        }
    }
}
