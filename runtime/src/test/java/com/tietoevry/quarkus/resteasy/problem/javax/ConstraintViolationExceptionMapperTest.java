package com.tietoevry.quarkus.resteasy.problem.javax;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.spi.nodenameprovider.JavaBeanProperty;
import org.hibernate.validator.spi.nodenameprovider.Property;
import org.hibernate.validator.spi.nodenameprovider.PropertyNodeNameProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ConstraintViolationExceptionMapperTest {

    final String INVALID = "a";
    final String VALID = "aaaaaaa";

    final ConstraintViolationExceptionMapper mapper = new ConstraintViolationExceptionMapper();
    final StubResourceInfo resourceInfo = StubResourceInfo.withDefaultValidator();

    @BeforeEach
    void setup() {
        mapper.resourceInfo = resourceInfo;
    }

    @Test
    void invalidPathParamShouldBeReported() {
        ConstraintViolationException exception = resourceInfo.validateParameters(INVALID, VALID, VALID, VALID,
                RequestBody.valid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations)
                .usingFieldByFieldElementComparator()
                .containsExactly(Violation.In.PATH.violation("length must be between 2 and 10", "param_name"));
    }

    @Test
    void invalidQueryParamShouldBeReported() {
        ConstraintViolationException exception = resourceInfo.validateParameters(VALID, INVALID, VALID, VALID,
                RequestBody.valid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations)
                .usingFieldByFieldElementComparator()
                .containsExactly(Violation.In.QUERY.violation("length must be between 3 and 10", "param_name"));
    }

    @Test
    void invalidHeaderParamShouldBeReported() {
        ConstraintViolationException exception = resourceInfo.validateParameters(VALID, VALID, INVALID, VALID,
                RequestBody.valid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations)
                .usingFieldByFieldElementComparator()
                .containsExactly(Violation.In.HEADER.violation("length must be between 4 and 10", "param_name"));
    }

    @Test
    @Disabled("FIXME")
    void invalidFormParamShouldBeReported() {
        ConstraintViolationException exception = resourceInfo.validateParameters(VALID, VALID, VALID, INVALID,
                RequestBody.valid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations)
                .usingFieldByFieldElementComparator()
                .containsExactly(Violation.In.FORM.violation("length must be between 5 and 10", "param_name"));
    }

    @Test
    @Disabled("FIXME")
    void invalidBodyShouldBeReported() {
        ConstraintViolationException exception = resourceInfo.validateParameters(VALID, VALID, VALID, VALID,
                RequestBody.invalid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations)
                .usingFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        Violation.In.BODY.violation("length must be between 6 and 10", "param_name"),
                        Violation.In.BODY.violation("length must be between 7 and 10", "items[1].param_name"),
                        Violation.In.BODY.violation("size must be between 1 and 10", "items2"),
                        Violation.In.BODY.violation("length must be between 8 and 10", "inner.param_name"));
    }

    @Test
    void shouldIncludeAllViolations() {
        ConstraintViolationException exception = resourceInfo.validateParameters(INVALID, INVALID, INVALID, INVALID,
                RequestBody.invalid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations).hasSize(8);
    }

    @Test
    @Disabled("FIXME")
    void shouldUseNamingStrategy() {
        StubResourceInfo resourceInfo = StubResourceInfo.withJsonPropertyAwareValidator();
        mapper.resourceInfo = resourceInfo;
        ConstraintViolationException exception = resourceInfo.validateParameters(VALID, VALID, VALID, VALID,
                RequestBody.invalid());

        List<Violation> violations = mapAndExtractViolations(exception);
        assertThat(violations)
                .usingFieldByFieldElementComparator()
                .contains(
                        Violation.In.BODY.violation("length must be between 6 and 10", "param_name_from_annotation"),
                        Violation.In.BODY.violation("length must be between 7 and 10", "items[1].param_name_from_annotation"),
                        Violation.In.BODY.violation("length must be between 8 and 10", "inner.param_name_from_annotation"));
    }

    private List<Violation> mapAndExtractViolations(ConstraintViolationException exception) {
        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMediaType()).isEqualTo(HttpProblem.MEDIA_TYPE);
        HttpProblem problem = (HttpProblem) response.getEntity();
        return (List<Violation>) problem.getParameters().get("violations");
    }

    static class StubResourceInfo implements ResourceInfo {

        private final Validator validator;

        StubResourceInfo(Validator validator) {
            this.validator = validator;
        }

        public static StubResourceInfo withDefaultValidator() {
            return new StubResourceInfo(Validation.byProvider(HibernateValidator.class)
                    .configure()
                    .buildValidatorFactory()
                    .getValidator());
        }

        public static StubResourceInfo withJsonPropertyAwareValidator() {
            return new StubResourceInfo(Validation.byProvider(HibernateValidator.class)
                    .configure()
                    .propertyNodeNameProvider(new JacksonPropertyNodeNameProvider())
                    .buildValidatorFactory()
                    .getValidator());
        }

        @Path("/{param_name}")
        public ConstraintViolationException validateParameters(
                @Valid @PathParam("param_name") @Length(min = 2, max = 10) String pathParam,
                @Valid @QueryParam("param_name") @Length(min = 3, max = 10) String queryParam,
                @Valid @HeaderParam("param_name") @Length(min = 4, max = 10) String headerParam,
                @Valid @FormParam("param_name") @Length(min = 5, max = 10) String formParam,
                @Valid RequestBody requestBody) {

            Set<ConstraintViolation<StubResourceInfo>> violations = validator.forExecutables().validateParameters(
                    this,
                    this.getResourceMethod(),
                    new Object[] { pathParam, queryParam, headerParam, formParam, requestBody });
            return new ConstraintViolationException(violations);
        }

        @Override
        public Method getResourceMethod() {
            try {
                return this.getClass().getMethod("validateParameters", String.class, String.class, String.class,
                        String.class, RequestBody.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getResourceClass() {
            return this.getClass();
        }

    }

    static class RequestBody {

        static RequestBody valid() {
            RequestBody validBody = new RequestBody();
            validBody.param_name = "1234567";
            validBody.items = Collections.singletonList(Item.valid());
            validBody.items2 = Collections.singletonList(Item.valid());
            validBody.inner.param_name = "123456789";
            return validBody;
        }

        static RequestBody invalid() {
            RequestBody invalidBody = new RequestBody();
            invalidBody.items = Arrays.asList(
                    Item.valid(),
                    Item.invalid());
            return invalidBody;
        }

        @Length(min = 6, max = 10)
        @JsonProperty("param_name_from_annotation")
        String param_name = "";

        @Valid
        List<Item> items;

        @Size(min = 1, max = 10)
        List<Item> items2 = Collections.emptyList();

        static class Item {

            static Item valid() {
                Item item = new Item();
                item.param_name = "12345678";
                return item;
            }

            static Item invalid() {
                return new Item();
            }

            @Length(min = 7, max = 10)
            @JsonProperty("param_name_from_annotation")
            String param_name = "";
        }

        @Valid
        InnerStubBody inner = new InnerStubBody();

        static class InnerStubBody {
            @Length(min = 8, max = 10)
            @JsonProperty("param_name_from_annotation")
            String param_name = "";
        }
    }

    /**
     * Validation takes @JsonProperty annotations into account.
     */
    static class JacksonPropertyNodeNameProvider implements PropertyNodeNameProvider {

        ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String getName(Property property) {
            if (property instanceof JavaBeanProperty) {
                return getJavaBeanPropertyName((JavaBeanProperty) property);
            }

            return getDefaultName(property);
        }

        private String getJavaBeanPropertyName(JavaBeanProperty property) {
            JavaType type = objectMapper.constructType(property.getDeclaringClass());
            BeanDescription desc = objectMapper.getSerializationConfig().introspect(type);

            return desc.findProperties()
                    .stream()
                    .filter(prop -> prop.getInternalName().equals(property.getName()))
                    .map(BeanPropertyDefinition::getName)
                    .findFirst()
                    .orElse(property.getName());
        }

        private String getDefaultName(Property property) {
            return property.getName();
        }
    }
}
