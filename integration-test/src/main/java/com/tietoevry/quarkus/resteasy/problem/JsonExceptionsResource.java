package com.tietoevry.quarkus.resteasy.problem;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/throw/json/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JsonExceptionsResource {

    @POST
    public void throwProblem(TestRequestBody body) {
    }

    public static final class TestRequestBody {
        public int key;
    }

}