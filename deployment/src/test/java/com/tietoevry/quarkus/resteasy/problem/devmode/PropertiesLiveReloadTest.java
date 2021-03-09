package com.tietoevry.quarkus.resteasy.problem.devmode;

import io.quarkus.test.QuarkusDevModeTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.MDC;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class PropertiesLiveReloadTest {

    static final String ORIGINAL_PROPERTY_NAME = "field-from-properties";
    static final String NEW_PROPERTY_NAME = "another-field-from-properties";

    static final String PROPERTY_VALUE = "123";

    @RegisterExtension
    static final QuarkusDevModeTest test = new QuarkusDevModeTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(TestMdcResource.class)
                    .addAsResource("application.properties"));

    @Test
    public void includeMdcPropertiesConfigChangeShouldBeApplied() {
        expectOriginalMdcPropertyOnly();

        whenAppConfigurationIsModified();

        expectNewMdcPropertyOnly();
    }

    private void expectOriginalMdcPropertyOnly() {
        when().get("/throw-exception").then()
                .statusCode(500)
                .body(ORIGINAL_PROPERTY_NAME, is(PROPERTY_VALUE))
                .body(NEW_PROPERTY_NAME, is(nullValue()));
    }

    private void whenAppConfigurationIsModified() {
        test.modifyResourceFile("application.properties",
                propertiesFileContent -> propertiesFileContent.replace(ORIGINAL_PROPERTY_NAME, NEW_PROPERTY_NAME));
    }

    private void expectNewMdcPropertyOnly() {
        when().get("/throw-exception").then()
                .statusCode(500)
                .body(ORIGINAL_PROPERTY_NAME, is(nullValue()))
                .body(NEW_PROPERTY_NAME, is(PROPERTY_VALUE));
    }

    @Path("/throw-exception")
    @Produces(MediaType.APPLICATION_JSON)
    public static class TestMdcResource {

        @GET
        public String setMdcAndThrow() {
            MDC.put(ORIGINAL_PROPERTY_NAME, PROPERTY_VALUE);
            MDC.put(NEW_PROPERTY_NAME, PROPERTY_VALUE);
            throw new RuntimeException("mdc test");
        }

    }
}