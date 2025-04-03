package io.quarkiverse.resteasy.problem.client;

import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import io.quarkiverse.resteasy.problem.HttpProblem;

/**
 * Utility which can be used as @Provider for Rest Clients along with @RegisterRestClient
 */
public class HttpProblemClientExceptionMapper implements ResponseExceptionMapper<RuntimeException> {

    @Override
    public RuntimeException toThrowable(Response response) {
        if (!HttpProblem.MEDIA_TYPE.equals(response.getMediaType())) {
            return null; // Let others handle non-problem formats
        }

        HttpProblem returnedProblem = response.readEntity(HttpProblem.class);

        // instance must be nullified, otherwise it will be propagated as-is
        return HttpProblem.builder(returnedProblem)
                .withInstance(null)
                .build();
    }

}
