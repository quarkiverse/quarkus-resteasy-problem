package com.tietoevry.quarkus.resteasy.problem;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.MDC;

@Path("/throw/mdc/")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class MdcResource {

    @GET
    public void setMdcAndThrow() {
        MDC.put("uuid", "30a48c9e");
        MDC.put("field-from-properties", "123");
        throw new RuntimeException("mdc test");
    }

}
