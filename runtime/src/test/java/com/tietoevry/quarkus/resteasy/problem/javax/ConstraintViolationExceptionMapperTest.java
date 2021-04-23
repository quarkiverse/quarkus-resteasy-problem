package com.tietoevry.quarkus.resteasy.problem.javax;

import static org.assertj.core.api.Assertions.assertThat;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConstraintViolationExceptionMapperTest {

    static final Validator VALIDATOR = Validation.byProvider(HibernateValidator.class)
            .configure()
            .buildValidatorFactory()
            .getValidator();

    ConstraintViolationExceptionMapper mapper = new ConstraintViolationExceptionMapper();
    ResourceInfoStub resourceInfoStub;

    @BeforeEach
    void setup() {
        resourceInfoStub = new ResourceInfoStub();
        mapper.resourceInfo = resourceInfoStub;
    }

    @Test
    void shouldIncludeAllViolations() {
        ConstraintViolationException exception = createValidationException();
        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMediaType()).isEqualTo(HttpProblem.MEDIA_TYPE);
        HttpProblem problem = (HttpProblem) response.getEntity();
        List<Violation> violations = (List<Violation>) problem.getParameters().get("violations");
        assertThat(violations)
                .usingFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        Violation.inQuery("length must be between 10 and 15", "param_name"),
                        Violation.inHeader("length must be between 10 and 15", "param_name3"),
                        Violation.inPath("length must be between 10 and 15", "param_name4"),
                        Violation.inBody("must be greater than or equal to 15", "phraseName"),
                        Violation.inBody("must be greater than or equal to 15", "innerObj.phraseName"));
    }

    private ConstraintViolationException createValidationException() {
        final Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.forExecutables()
                .validateParameters(
                        resourceInfoStub,
                        resourceInfoStub.getResourceMethod(),
                        new Object[] { "a", "a", "a", "a", new ResourceInfoStub.StubBody() },
                        new Class[0]);

        return new ConstraintViolationException(constraintViolations);
    }

    static class ResourceInfoStub implements ResourceInfo {

        @Override
        public Method getResourceMethod() {
            try {
                final Class<String> string = String.class;
                final Class<StubBody> object = StubBody.class;
                return this.getClass().getMethod("endpoint", string, string, string, string, object);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getResourceClass() {
            return this.getClass();
        }

        @Path("{param_name4}")
        public void endpoint(@Valid @QueryParam("param_name") @Length(min = 10, max = 15) String invalidQueryParam,
                @Valid @QueryParam("param_name2") @Length(min = 1, max = 500) String validQueryParam,
                @Valid @HeaderParam("param_name3") @Length(min = 10, max = 15) String invalidHeaderParam,
                @Valid @PathParam("param_name4") @Length(min = 10, max = 15) String invalidPathParam,
                @Valid StubBody invalidPayload) {
        }

        public static class StubBody {
            @Min(15)
            public int phraseName;

            @Valid
            InnerStubBody innerObj = new InnerStubBody();

            public static class InnerStubBody {
                @Min(15)
                public int phraseName;
            }
        }
    }
}
