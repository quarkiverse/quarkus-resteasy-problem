package com.tietoevry.quarkus.resteasy.problem.jackson;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.util.stream.Collectors;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;

/**
 * Mapper for Jackson InvalidFormatException, which is more specialised version of JsonProcessingException
 */
@Priority(Priorities.USER)
public final class InvalidFormatExceptionMapper extends ExceptionMapperBase<InvalidFormatException> {

    @Override
    protected HttpProblem toProblem(InvalidFormatException exception) {
        String field = exception.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));

        return HttpProblem.builder()
                .withStatus(Response.Status.BAD_REQUEST)
                .withTitle(Response.Status.BAD_REQUEST.getReasonPhrase())
                .withDetail(exception.getOriginalMessage())
                .with("field", field)
                .build();
    }
}
