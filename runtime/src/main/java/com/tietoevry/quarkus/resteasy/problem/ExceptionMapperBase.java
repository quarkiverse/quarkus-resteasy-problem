package com.tietoevry.quarkus.resteasy.problem;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.apiguardian.api.API;
import org.slf4j.LoggerFactory;
import org.zalando.problem.Problem;
import org.zalando.problem.StatusType;

/**
 * Base class for all ExceptionMappers, takes care of mapping Exceptions to Problems, triggering post-processing stage, and
 * creating final JaxRS Response.
 */
public abstract class ExceptionMapperBase<E extends Throwable> implements ExceptionMapper<E> {

    public static final MediaType APPLICATION_PROBLEM_JSON = new MediaType("application", "problem+json");

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

    @Override
    public final Response toResponse(E exception) {
        Problem problem = toProblem(exception);
        for (ProblemProcessor processor : processors) {
            problem = processor.apply(problem, exception);
        }
        return toResponse(problem, exception);
    }

    protected abstract Problem toProblem(E exception);

    /**
     * This is an internal API. It may be changed or removed without notice in any release.
     */
    @API(status = API.Status.INTERNAL)
    protected Response toResponse(Problem problem, E originalException) {
        int statusCode = Optional.ofNullable(problem.getStatus())
                .map(StatusType::getStatusCode)
                .orElse(500);

        return Response
                .status(statusCode)
                .type(APPLICATION_PROBLEM_JSON)
                .entity(problem)
                .build();
    }

}
