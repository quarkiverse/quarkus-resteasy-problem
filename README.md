# Problem Details for HTTP APIs (RFC-7807) implementation for Quarkus / RESTeasy.

[![Release](https://img.shields.io/maven-central/v/com.tietoevry.quarkus/quarkus-resteasy-problem)](https://search.maven.org/artifact/com.tietoevry.quarkus/quarkus-resteasy-problem)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/blob/master/LICENSE.txt)
![JVM](https://img.shields.io/badge/JVM-1.8+-green.svg)
![Quarkus](https://img.shields.io/badge/Quarkus-1.4.2%20+-green.svg)

[![Build status](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/unit-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions)
[![Build status](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/integration-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions)
[![Build status](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/native-mode-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions)

[RFC7807 Problem](https://tools.ietf.org/html/rfc7807) extension for Quarkus RESTeasy/JaxRS applications, inspired by [Zalando Problem library](https://github.com/zalando/problem). \
This extension registers few JaxRS Exceptions Mappers for common exceptions thrown by Quarkus apps, which turn exceptions into standardized HTTP responses described in RFC, with content type `application/problem+json`. See [Built-in exception mappers](#built-in-exception-mappers) section for more details.

Supports:
- _quarkus-resteasy-jackson_ and _quarkus-resteasy-jsonb_ for Quarkus 1.4.2 and newer (including 2.0.0.Alpha1)
- _quarkus-resteasy-reactive-jackson_ and _quarkus-resteasy-reactive-jsonb_ for Quarkus 1.11.6 and newer (including 2.0.0.Alpha1)
- JVM and native mode
- Java 8+

## Why you should use it?
This extension unifies (and simplifies) the way services handle and return REST API error messages.

From [RFC7807](https://tools.ietf.org/html/rfc7807):
```
HTTP [RFC7230] status codes are sometimes not sufficient to convey
enough information about an error to be helpful.  While humans behind
Web browsers can be informed about the nature of the problem with an
HTML [W3C.REC-html5-20141028] response body, non-human consumers of
so-called "HTTP APIs" are usually not.
```

You may also want to check [this article](https://dzone.com/articles/when-http-status-codes-are-not-enough-tackling-web) on RFC7807 practical usage.

## Usage
Create a new Quarkus project with the following command (you can also use `resteasy-jsonb` or reactive equivalents: `resteasy-reactive-jackson` / `resteasy-reactive-jsonb`):
```shell
mvn io.quarkus:quarkus-maven-plugin:2.0.0.Alpha1.Final:create \
    -DprojectGroupId=problem \
    -DprojectArtifactId=quarkus-resteasy-problem-playground \
    -DclassName="problem.HelloResource" \
    -Dpath="/hello" \
    -Dextensions="resteasy,resteasy-jackson"
cd quarkus-resteasy-problem-playground
```

Now add this to your `pom.xml`:
```xml
<dependency>
    <groupId>com.tietoevry.quarkus</groupId>
    <artifactId>quarkus-resteasy-problem</artifactId>
    <version>${resteasy-problem.version}</version>
</dependency>
```

Run the application with: `./mvnw compile quarkus:dev`, and you will find `resteasy-problem` in the logs:
<pre>
Installed features: [cdi, resteasy, resteasy-jackson, <b><u>resteasy-problem</u></b>]
</pre>

Now you can throw `HttpProblem`s (using builder or a subclass), JaxRS exceptions (e.g `NotFoundException`) or `ThrowableProblem`s from Zalando library:

```java
package problem;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/hello")
public class HelloResource {

    @GET
    public String hello() {
        throw new HelloProblem("rfc7807-by-example");
    }

    static class HelloProblem extends HttpProblem {
        HelloProblem(String message) {
            super(builder()
                    .withTitle("Bad hello request")
                    .withStatus(Response.Status.BAD_REQUEST)
                    .withDetail(message)
                    .withHeader("X-RFC7807-Message", message)
                    .with("hello", "world"));
        }
    }
}
```

Open [http://localhost:8080/hello](http://localhost:8080/hello) in your browser, and you should see this response:

```json
HTTP/1.1 400 Bad Request
X-RFC7807-Message: rfc7807-by-example
Content-Type: application/problem+json
        
{
    "status": 400,
    "title": "Bad hello request",
    "detail": "rfc7807-by-example",
    "instance": "/hello",
    "hello": "world"
}
```

This extension will also produce the following log message:
```
10:53:48 INFO [http-problem] (executor-thread-1) status=400, title="Bad hello request", detail="rfc7807-by-example"
```
Exceptions transformed into http 500s (aka server errors) will be logged as `ERROR`, including full stacktrace.

More info on throwing problems: [zalando/problem usage](https://github.com/zalando/problem#usage)

## Built-in exception mappers
This extension provides mappers for common exceptions thrown by Quarkus apps.\
Some of them are thrown by Quarkus itself in certain situations (i.e failed authorization).
You can throw them from controllers or business logic as well (i.e `NotFoundException` as in the example above).

| Exception                                | Thrown by / when                 | Example JSON response                        |
|------------------------------------------|--------------------------------|------------------------------------------------|
| `sec.AuthenticationFailedException`      | Missing or invalid JWT         | `{ "status" : 401, ... }`                      |
| `sec.UnauthorizedException`              | Missing or invalid JWT         | `{ "status" : 401, ... }`                      |
| `sec.ForbiddenException`                 | `@RolesAllowed` not satisfied  | `{ "status" : 403, ... }`                      |
| `javax.ConstraintViolationException`     | Hibernate Validator (`@Valid`) | `{ "status" : 400, "violations" : [{...}] }`     |
| `javax.ValidationException`              | user or Quarkus                | `{ "status" : 400, ... }`                      |
| `javax.RedirectionException`             | user or Quarkus                | `{ "status" : 3XX, ... }` + `Location` header  |
| `jaxrs.NotFoundException`                | RESTeasy, user                 | `{ "status" : 404, ... }`                      |
| `jaxrs.WebApplicationException(status)`  | user or Quarkus                | `{ "status" : <status>, ... }`                 |
| `HttpProblem(status)`                    | user                           | `{ "status" : <status>, ... }`                 |
| `zalando.Problem(status)`                | user                           | `{ "status" : <status>, ... }`                 |
| `Exception`                              | user or Quarkus                | `{ "status" : 500, ... }`                      |

There's also top-level mapper for `Exception` class, which will convert all unhandled exceptions to HTTP 500 response.

## Configuration options
All configuration options are build-time properties, meaning that you cannot override them in the runtime (i.e via environment variables).

- Include MDC properties in the API response (you have to provide those properties to MDC using `MDC.put`)
```
quarkus.resteasy.problem.include-mdc-properties=uuid,application,version
```
Result:
```json
{
  "status": 500,
  "title": "Internal Server Error",
  "uuid": "d79f8cfa-ef5b-4501-a2c4-8f537c08ec0c",
  "application": "awesome-microservice",
  "version": "1.0"
}
```

- Enable Smallrye (Microprofile) metrics for http error counters. Requires `quarkus-smallrye-metrics` in the classpath.

Please note that if you use `quarkus-micrometer-registry-prometheus` you don't need this feature - http error metrics will be produced regardless of this setting or presence of this extension.

```
quarkus.resteasy.problem.metrics.enabled=true
```
Result:
```
GET /metrics
application_http_error_total{status="401"} 3.0
application_http_error_total{status="500"} 5.0
```

- Tuning logging
```
quarkus.log.category.http-problem.level=INFO # default: all problems are logged
quarkus.log.category.http-problem.level=ERROR # only HTTP 5XX problems are logged
quarkus.log.category.http-problem.level=OFF # disables all problems-related logging
```

## Troubles?

If you have questions, concerns, bug reports, etc, please file an issue in this repository's Issue Tracker. You may also want to have a look at [troubleshooting FAQ](./TROUBLESHOOTING.md).

## Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change.
For more details check the [contribution guidelines](./CONTRIBUTING.md).