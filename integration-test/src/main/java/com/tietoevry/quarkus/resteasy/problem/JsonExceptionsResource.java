package com.tietoevry.quarkus.resteasy.problem;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("/throw/json/")
@Consumes(MediaType.APPLICATION_JSON)
public class JsonExceptionsResource {

    @POST
    public void throwValidationException(TestRequestBody body) {
    }

    public static final class TestRequestBody {
        public UUID uuid_field_1;

        public Nested nested;

        public List<Nested> collection;

        public static final class Nested {
            public UUID uuid_field_2;
        }
    }

}