package com.tietoevry.quarkus.resteasy.problem;

import org.slf4j.LoggerFactory;
import org.zalando.problem.Problem;
import org.zalando.problem.StatusType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ExceptionMapperBase<E extends Throwable> implements ExceptionMapper<E> {

    private static final MediaType APPLICATION_PROBLEM_JSON = new MediaType("application", "problem+json");

    private static final List<ProblemProcessor> processors = new CopyOnWriteArrayList<>();

    static {
        registerProcessor(new LoggingProcessor(LoggerFactory.getLogger("http-problem")));
    }

    static synchronized void registerProcessor(ProblemProcessor processor) {
        processors.add(processor);
        processors.sort(Comparator.comparingInt(ProblemProcessor::priority).reversed());
    }

    @Override
    public final Response toResponse(E exception) {
        var problem = toProblem(exception);
        for (var processor : processors) {
            problem = processor.apply(problem, exception);
        }
        return toResponse(problem);
    }

    private Response toResponse(Problem problem) {
        var statusCode = Optional.ofNullable(problem.getStatus())
                .map(StatusType::getStatusCode)
                .orElse(500);

        return Response
                .status(statusCode)
                .type(APPLICATION_PROBLEM_JSON)
                .entity(problem)
                .build();
    }

    protected abstract Problem toProblem(E exception);
}
