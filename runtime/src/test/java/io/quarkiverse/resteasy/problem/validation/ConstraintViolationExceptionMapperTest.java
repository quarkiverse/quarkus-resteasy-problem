package io.quarkiverse.resteasy.problem.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ParameterNameProvider;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Response;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.spi.nodenameprovider.JavaBeanProperty;
import org.hibernate.validator.spi.nodenameprovider.Property;
import org.hibernate.validator.spi.nodenameprovider.PropertyNodeNameProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import io.quarkiverse.resteasy.problem.HttpProblem;

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
    void nullConstraintViolationsSetShouldNotCrash() {
        ConstraintViolationException exception = new ConstraintViolationException("omg!", null);

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations).isEmpty();
    }

    @Test
    void invalidPathParamShouldBeReported() {
        ConstraintViolationException exception = resourceInfo.validateParameters(INVALID, VALID, VALID, VALID,
                RequestBody.valid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(Violation.In.path.field("param_name").message("length must be between 2 and 10"));
    }

    @Test
    void invalidQueryParamShouldBeReported() {
        ConstraintViolationException exception = resourceInfo.validateParameters(VALID, INVALID, VALID, VALID,
                RequestBody.valid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(Violation.In.query.field("param_name").message("length must be between 3 and 10"));
    }

    @Test
    void invalidHeaderParamShouldBeReported() {
        ConstraintViolationException exception = resourceInfo.validateParameters(VALID, VALID, INVALID, VALID,
                RequestBody.valid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(Violation.In.header.field("param_name").message("length must be between 4 and 10"));
    }

    @Test
    void invalidFormParamShouldBeReported() {
        ConstraintViolationException exception = resourceInfo.validateParameters(VALID, VALID, VALID, INVALID,
                RequestBody.valid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(Violation.In.form.field("param_name").message("length must be between 5 and 10"));
    }

    @Test
    void invalidBodyShouldBeReported() {
        ConstraintViolationException exception = resourceInfo.validateParameters(VALID, VALID, VALID, VALID,
                RequestBody.invalid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        Violation.In.body.field("param_name")
                                .message("length must be between 6 and 10"),
                        Violation.In.body.field("items[1].param_name")
                                .message("length must be between 7 and 10"),
                        Violation.In.body.field("items2")
                                .message("size must be between 1 and 10"),
                        Violation.In.body.field("inner.param_name")
                                .message("length must be between 8 and 10"));
    }

    @Test
    void shouldIncludeAllViolations() {
        ConstraintViolationException exception = resourceInfo.validateParameters(INVALID, INVALID, INVALID, INVALID,
                RequestBody.invalid());

        List<Violation> violations = mapAndExtractViolations(exception);

        assertThat(violations).hasSize(8);
    }

    @Test
    void shouldUseJsonPropertyNamingStrategy() {
        StubResourceInfo resourceInfo = StubResourceInfo.withJsonPropertyAwareValidator();
        mapper.resourceInfo = resourceInfo;
        ConstraintViolationException exception = resourceInfo.validateParameters(VALID, VALID, VALID, VALID,
                RequestBody.invalid());

        List<Violation> violations = mapAndExtractViolations(exception);
        assertThat(violations)
                .usingRecursiveFieldByFieldElementComparator()
                .contains(
                        Violation.In.body.field("param_name_from_annotation")
                                .message("length must be between 6 and 10"),
                        Violation.In.body.field("items[1].param_name_from_annotation")
                                .message("length must be between 7 and 10"),
                        Violation.In.body.field("inner.param_name_from_annotation")
                                .message("length must be between 8 and 10"),
                        Violation.In.body.field("items2")
                                .message("size must be between 1 and 10"));
    }

    @Test
    void customParameterNamingStrategyShouldBeTolerated() {
        StubResourceInfo resourceInfo = StubResourceInfo.withCustomParameterNameProvider();
        mapper.resourceInfo = resourceInfo;
        ConstraintViolationException exception = resourceInfo.validateParameters(INVALID, VALID, VALID, VALID,
                RequestBody.invalid());

        List<Violation> violations = mapAndExtractViolations(exception);
        assertThat(violations)
                .usingRecursiveFieldByFieldElementComparator()
                .contains(
                        Violation.In.unknown.field("firstParam")
                                .message("length must be between 2 and 10"),
                        Violation.In.unknown.field("fifthParam.param_name")
                                .message("length must be between 6 and 10"),
                        Violation.In.unknown.field("fifthParam.items[1].param_name")
                                .message("length must be between 7 and 10"),
                        Violation.In.unknown.field("fifthParam.inner.param_name")
                                .message("length must be between 8 and 10"));
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

        public static StubResourceInfo withCustomParameterNameProvider() {
            return new StubResourceInfo(Validation.byProvider(HibernateValidator.class)
                    .configure()
                    .parameterNameProvider(new ParameterNameProvider() {
                        private final List<String> hardcodedNames = Arrays.asList("firstParam", "secondParam", "thirdParam",
                                "forthParam", "fifthParam");

                        @Override
                        public List<String> getParameterNames(Constructor<?> constructor) {
                            return hardcodedNames;
                        }

                        @Override
                        public List<String> getParameterNames(Method method) {
                            return hardcodedNames;
                        }
                    })
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
