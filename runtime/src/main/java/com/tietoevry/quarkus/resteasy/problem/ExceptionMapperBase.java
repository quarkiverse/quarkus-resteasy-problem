package com.tietoevry.quarkus.resteasy.problem;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.LoggerFactory;
import org.zalando.problem.Problem;
import org.zalando.problem.StatusType;

public abstract class ExceptionMapperBase<E extends Throwable> implements ExceptionMapper<E> {

    private static final MediaType APPLICATION_PROBLEM_JSON = new MediaType("application", "problem+json");
    private static final MediaType APPLICATION_PROBLEM_XML = new MediaType("application", "problem+xml");

    private static final List<ProblemProcessor> processors = new CopyOnWriteArrayList<>();

    static {
        resetProcessors();
    }

    static synchronized void resetProcessors() {
        processors.clear();
        registerProcessor(new LoggingProcessor(LoggerFactory.getLogger("http-problem")));
    }

    static synchronized void registerProcessor(ProblemProcessor processor) {
        processors.add(processor);
        processors.sort(Comparator.comparingInt(ProblemProcessor::priority).reversed());
    }

    private static boolean xmlProblemEnabled = false;

    public static void enableXmlProblemSupport() {
        xmlProblemEnabled = true;
    }

    @Context
    HttpHeaders headers;

    @Override
    public final Response toResponse(E exception) {
        Problem problem = toProblem(exception);
        for (ProblemProcessor processor : processors) {
            problem = processor.apply(problem, exception);
        }
        return toResponse(problem);
    }

    protected abstract Problem toProblem(E exception);

    private Response toResponse(Problem problem) {
        int statusCode = Optional.ofNullable(problem.getStatus())
                .map(StatusType::getStatusCode)
                .orElse(500);

        MediaType mediaType = mediaType();
        Object entity = (xmlProblemEnabled && mediaType.equals(APPLICATION_PROBLEM_XML))
                ? XmlProblem.serialize(problem)
                : problem;

        return Response
                .status(statusCode)
                .type(mediaType)
                .entity(entity)
                .build();
    }

    private MediaType mediaType() {
        if (clientAccepts(APPLICATION_JSON_TYPE)) {
            return APPLICATION_PROBLEM_JSON;
        }
        if (clientAccepts(APPLICATION_XML_TYPE)) {
            return APPLICATION_PROBLEM_XML;
        }
        return APPLICATION_PROBLEM_JSON;
    }

    private boolean clientAccepts(MediaType mediaType) {
        return headers.getAcceptableMediaTypes().stream()
                .anyMatch(accept -> accept.isCompatible(mediaType));
    }

}
