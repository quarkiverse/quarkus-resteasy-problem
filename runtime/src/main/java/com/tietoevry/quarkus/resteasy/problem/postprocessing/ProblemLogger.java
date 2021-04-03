package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.zalando.problem.Problem;

/**
 * Logs problems with ERROR (for HTTP 5XX) or INFO (other exceptions) log level. In case of ERROR (HTTP 5XX) stack trace is
 * printed as well.
 */
final class ProblemLogger implements ProblemPostProcessor {

    private final Logger logger;
    private final ObjectMapper mapper = new ObjectMapper();

    ProblemLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Problem apply(Problem problem, ProblemContext context) {
        if (!logger.isErrorEnabled()) {
            return problem;
        }

        Objects.requireNonNull(problem.getStatus());

        if (problem.getStatus().getStatusCode() >= 500) {
            if (logger.isErrorEnabled()) {
                logger.error(serialize(problem), context.cause);
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(serialize(problem));
            }
        }
        return problem;
    }

    private String serialize(Problem problem) {
        return Stream.concat(
                Stream.of(
                        (problem.getStatus() == null) ? null : ("status=" + problem.getStatus().getStatusCode()),
                        (problem.getTitle() == null) ? null : ("title=\"" + problem.getTitle() + "\""),
                        (problem.getDetail() == null) ? null : ("detail=\"" + problem.getDetail() + "\""),
                        (problem.getInstance() == null) ? null : ("instance=\"" + problem.getInstance() + "\""),
                        "about:blank".equals(problem.getType().toString())
                                ? null
                                : "type=" + problem.getType().toString()),
                problem.getParameters().entrySet().stream().map(this::serializeParameter))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    private String serializeParameter(Map.Entry<String, Object> param) {
        try {
            return param.getKey() + "=" + mapper.writeValueAsString(param.getValue());
        } catch (JsonProcessingException e) {
            return param.getKey() + "=" + param.getValue().toString();
        }
    }

    @Override
    public int priority() {
        return 101;
    }
}
