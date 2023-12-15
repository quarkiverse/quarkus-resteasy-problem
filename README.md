# Problem Details for HTTP APIs (RFC-7807) implementation for Quarkus / RESTeasy.

[![Release](https://img.shields.io/maven-central/v/com.tietoevry.quarkus/quarkus-resteasy-problem/3?label=quarkus-resteasy-problem)](https://search.maven.org/search?q=g:com.tietoevry.quarkus%20AND%20a:quarkus-resteasy-problem%20AND%20v:3*) 
[![Quarkus](https://img.shields.io/badge/Quarkus-3.0.0+-important.svg)](https://github.com/quarkusio/quarkus/releases/tag/3.0.0.Final)
![Quarkus](https://img.shields.io/badge/Java%2011+-blue.svg) 

[![Release](https://img.shields.io/maven-central/v/com.tietoevry.quarkus/quarkus-resteasy-problem/2?label=quarkus-resteasy-problem)](https://search.maven.org/search?q=g:com.tietoevry.quarkus%20AND%20a:quarkus-resteasy-problem%20AND%20v:2*) 
[![Quarkus](https://img.shields.io/badge/Quarkus-2.0.0+-important.svg)](https://github.com/quarkusio/quarkus/releases/tag/2.16.6.Final)
![Quarkus](https://img.shields.io/badge/Java%2011+-blue.svg) 

[![Release](https://img.shields.io/maven-central/v/com.tietoevry.quarkus/quarkus-resteasy-problem/1?label=quarkus-resteasy-problem)](https://search.maven.org/search?q=g:com.tietoevry.quarkus%20AND%20a:quarkus-resteasy-problem%20AND%20v:1*)
[![Quarkus](https://img.shields.io/badge/Quarkus-1.4.0+-important.svg)](https://github.com/quarkusio/quarkus/releases/tag/1.13.7.Final)
![Quarkus](https://img.shields.io/badge/Java%208+-blue.svg) 

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/tietoevry/quarkus-resteasy-problem/blob/master/LICENSE.txt)

[![Build status](https://github.com/tietoevry/quarkus-resteasy-problem/actions/workflows/unit-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/unit-tests.yaml)
[![Build status](https://github.com/tietoevry/quarkus-resteasy-problem/actions/workflows/integration-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/integration-tests.yaml)
[![Build status](https://github.com/tietoevry/quarkus-resteasy-problem/actions/workflows/native-mode-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/native-mode-tests.yaml)
[![Compatibility with latest stable Quarkus](https://github.com/tietoevry/quarkus-resteasy-problem/actions/workflows/latest-stable-compatibility-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/latest-stable-compatibility-tests.yaml)

[RFC7807 Problem](https://tools.ietf.org/html/rfc7807) extension for Quarkus RESTeasy/JaxRS applications. It maps Exceptions to `application/problem+json` HTTP responses. Inspired by [Zalando Problem library](https://github.com/zalando/problem).

This extension supports:
- Quarkus 1, 2 and 3
- `quarkus-resteasy-jackson` and `quarkus-resteasy-jsonb`
- `quarkus-resteasy-reactive-jackson` and `quarkus-resteasy-reactive-jsonb`
- JVM and native mode

## Why you should use this extension?
- __consistency__ - it unifies your REST API error messages, and gives it much needed consistency, no matter which JSON provider (Jackson vs JsonB) or paradigm (classic/blocking vs reactive) you're using.   

- __predictability__ - no matter what kind of exception is thrown: expected (thrown by you on purpose), or unexpected (not thrown 'by design') - your API consumer gets similar, repeatable experience.  

- __safety__ - it helps prevent leakage of some implementation details like stack-traces, DTO/resource class names etc.

- __time-saving__ - in most cases you will not have to implement your own JaxRS `ExceptionMapper`s anymore, which makes your app smaller, and less error-prone. 

See [Built-in Exception Mappers Wiki](https://github.com/tietoevry/quarkus-resteasy-problem/wiki#built-in-exception-mappers) for more details.

From [RFC7807](https://tools.ietf.org/html/rfc7807):
```
HTTP [RFC7230] status codes are sometimes not sufficient to convey
enough information about an error to be helpful.  While humans behind
Web browsers can be informed about the nature of the problem with an
HTML [W3C.REC-html5-20141028] response body, non-human consumers of
so-called "HTTP APIs" are usually not.
```

## Usage
### Quarkus 3.X / Java 11+
Make sure JDK 11 is in your PATH, then run:
```shell
mvn io.quarkus:quarkus-maven-plugin:3.6.3:create \
    -DprojectGroupId=problem \
    -DprojectArtifactId=quarkus-resteasy-problem-playground \
    -DclassName="problem.HelloResource" \
    -Dpath="/hello" \
    -Dextensions="resteasy,resteasy-jackson"
cd quarkus-resteasy-problem-playground
./mvnw quarkus:add-extension -Dextensions="com.tietoevry.quarkus:quarkus-resteasy-problem:3.1.0"
```
Or add the following dependency to `pom.xml` in existing project:
```xml
<dependency>
    <groupId>com.tietoevry.quarkus</groupId>
    <artifactId>quarkus-resteasy-problem</artifactId>
    <version>3.1.0</version>
</dependency>
```

<details>
    <summary>Quarkus 2.X / Java 11+</summary>

  Make sure JDK 11 is in your PATH, then run:
  ```shell 
  mvn io.quarkus:quarkus-maven-plugin:2.16.10.Final:create \
      -DprojectGroupId=problem \
      -DprojectArtifactId=quarkus-resteasy-problem-playground \
      -DclassName="problem.HelloResource" \
      -Dpath="/hello" \
      -Dextensions="resteasy,resteasy-jackson"
  cd quarkus-resteasy-problem-playground
  ./mvnw quarkus:add-extension -Dextensions="com.tietoevry.quarkus:quarkus-resteasy-problem:2.2.0
  ```
  Or add the following dependency to `pom.xml` in existing project:
  ```xml
  <dependency>
      <groupId>com.tietoevry.quarkus</groupId>
      <artifactId>quarkus-resteasy-problem</artifactId>
      <version>2.2.0</version>
  </dependency>
  ```
</details>

<details>
    <summary>Quarkus 1.X / Java 1.8+</summary>
    
  Create a new Quarkus project with the following command:
  ```shell 
  mvn io.quarkus:quarkus-maven-plugin:1.13.7.Final:create \
      -DprojectGroupId=problem \
      -DprojectArtifactId=quarkus-resteasy-problem-playground \
      -DclassName="problem.HelloResource" \
      -Dpath="/hello" \
      -Dextensions="resteasy,resteasy-jackson,com.tietoevry.quarkus:quarkus-resteasy-problem:1.0.0"
  cd quarkus-resteasy-problem-playground
  ```
  Or add the following dependency to `pom.xml` in existing project:
  ```xml
  <dependency>
    <groupId>com.tietoevry.quarkus</groupId>
    <artifactId>quarkus-resteasy-problem</artifactId>
    <version>1.0.0</version>
  </dependency>
  ```
</details>

**Hint:** you can also use `resteasy-jsonb` or reactive equivalents: `resteasy-reactive-jackson` / `resteasy-reactive-jsonb` instead of `resteasy-jackson`


Once you run Quarkus: `./mvnw compile quarkus:dev`, and you will find `resteasy-problem` in the logs:
<pre>
Installed features: [cdi, resteasy, resteasy-jackson, <b><u>resteasy-problem</u></b>]
</pre>

Now you can throw `HttpProblem`s (using builder or a subclass), JaxRS exceptions (e.g `NotFoundException`) or `ThrowableProblem`s from Zalando library:

```java
package problem;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

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

You may also want to check [this article](https://dzone.com/articles/when-http-status-codes-are-not-enough-tackling-web) on RFC7807 practical usage.  
More on throwing problems: [zalando/problem usage](https://github.com/zalando/problem#usage)

## Configuration options

- (Build time) Include MDC properties in the API response. You have to provide those properties to MDC using `MDC.put`
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

- (Runtime) Changes default `400 Bad request` response status when `ConstraintViolationException` is thrown (e.g. by Hibernate Validator)
```
quarkus.resteasy.problem.constraint-violation.status=422
quarkus.resteasy.problem.constraint-violation.title=Constraint violation
```
Result:
```json
HTTP/1.1 422 Unprocessable Entity
Content-Type: application/problem+json

{
    "status": 422,
    "title": "Constraint violation",
    (...)
}
```

- (Build time) Enable Smallrye (Microprofile) metrics for http error counters. Requires `quarkus-smallrye-metrics` in the classpath.

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

- (Runtime) Tuning logging
```
quarkus.log.category.http-problem.level=INFO # default: all problems are logged
quarkus.log.category.http-problem.level=ERROR # only HTTP 5XX problems are logged
quarkus.log.category.http-problem.level=OFF # disables all problems-related logging
```

## Custom ProblemPostProcessor
If you want to intercept, change or augment a mapped `HttpProblem` before it gets serialized into raw HTTP response 
body, you can create a bean extending `ProblemPostProcessor`, and override `apply` method.

Example:
```java
@ApplicationScoped
class CustomPostProcessor implements ProblemPostProcessor {
    
    @Inject // acts like normal bean, DI works fine etc
    Validator validator;
    
    @Override
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        return HttpProblem.builder(problem)
                .with("injected_from_custom_post_processor", "hello world " + context.path)
                .build();
    }
    
}
```

## Troubles?

If you have questions, concerns, bug reports, etc, please file an issue in this repository's Issue Tracker. You may also want to have a look at [troubleshooting FAQ](./TROUBLESHOOTING.md).

## Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change.
For more details check the [contribution guidelines](./CONTRIBUTING.md).
