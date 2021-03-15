package com.tietoevry.quarkus.resteasy.problem.devmode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.MDC;

@Path("/throw-exception")
@Produces(MediaType.APPLICATION_JSON)
public class TestMdcResource {

    @GET
    public String setMdcAndThrow() {
        MDC.put(PropertiesLiveReloadTest.ORIGINAL_PROPERTY_NAME, PropertiesLiveReloadTest.PROPERTY_VALUE);
        MDC.put(PropertiesLiveReloadTest.NEW_PROPERTY_NAME, PropertiesLiveReloadTest.PROPERTY_VALUE);
        throw new RuntimeException("mdc test");
    }

}
