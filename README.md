# Problem Details for HTTP APIs (RFC-7807) implementation for Quarkus / RESTeasy.

[![Release](https://img.shields.io/maven-central/v/com.tietoevry.quarkus/quarkus-resteasy-problem/2?label=quarkus-resteasy-problem)](https://search.maven.org/search?q=g:com.tietoevry.quarkus%20AND%20a:quarkus-resteasy-problem%20AND%20v:2*) 
[![Quarkus](https://img.shields.io/badge/Quarkus-2.0.0+-important.svg)](https://github.com/quarkusio/quarkus/releases/tag/2.0.0.Final)
![Quarkus](https://img.shields.io/badge/Java%2011+-blue.svg) 

[![Release](https://img.shields.io/maven-central/v/com.tietoevry.quarkus/quarkus-resteasy-problem/1?label=quarkus-resteasy-problem)](https://search.maven.org/search?q=g:com.tietoevry.quarkus%20AND%20a:quarkus-resteasy-problem%20AND%20v:1*)
[![Quarkus](https://img.shields.io/badge/Quarkus-1.4%20&ndash;%201.13-important.svg)](https://github.com/quarkusio/quarkus/releases/tag/1.13.7.Final)
![Quarkus](https://img.shields.io/badge/Java%208+-blue.svg) 

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/blob/master/LICENSE.txt)

[![Build status](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/unit-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/unit-tests.yaml)
[![Build status](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/integration-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/integration-tests.yaml)
[![Build status](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/native-mode-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/native-mode-tests.yaml)
[![Compatibility with latest stable Quarkus](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/latest-stable-compatibility-tests.yaml/badge.svg)](https://github.com/TietoEVRY/quarkus-resteasy-problem/actions/workflows/latest-stable-compatibility-tests.yaml)

[RFC7807 Problem](https://tools.ietf.org/html/rfc7807) extension for Quarkus RESTeasy/JaxRS applications. It maps Exceptions to `application/problem+json` HTTP responses. Inspired by [Zalando Problem library](https://github.com/zalando/problem).

This extension supports:
- Quarkus 1.X and 2.X
- `quarkus-resteasy-jackson` and `quarkus-resteasy-jsonb`
- `quarkus-resteasy-reactive-jackson` and `quarkus-resteasy-reactive-jsonb`
- JVM and native mode

## Why you should use this extension?
- __consistency__ - it unifies your REST API error messages, and gives it much needed consistency, no matter which JSON provider (Jackson vs JsonB) or paradigm (classic/blocking vs reactive) you're using.   

- __predictability__ - no matter what kind of exception is thrown: expected (thrown by you on purpose), or unexpected (not thrown 'by design') - your API consumer gets similar, repeatable experience.  

- __safety__ - it helps prevent leakage of some implementation details like stack-traces, DTO/resource class names etc.

- __time-saving__ - in most cases you will not have to implement your own JaxRS `ExceptionMapper`s anymore, which makes your app smaller, and less error-prone. 

See [Built-in Exception Mappers Wiki](https://github.com/TietoEVRY/quarkus-resteasy-problem/wiki#built-in-exception-mappers) for more details.

From [RFC7807](https://tools.ietf.org/html/rfc7807):
```
HTTP [RFC7230] status codes are sometimes not sufficient to convey
enough information about an error to be helpful.  While humans behind
Web browsers can be informed about the nature of the problem with an
HTML [W3C.REC-html5-20141028] response body, non-human consumers of
so-called "HTTP APIs" are usually not.
```

## Usage
### Quarkus 2.X / Java 11+
Make sure JDK 11 is in your PATH, the run:
```shell
mvn io.quarkus:quarkus-maven-plugin:2.6.0.Final:create \
    -DprojectGroupId=problem \
    -DprojectArtifactId=quarkus-resteasy-problem-playground \
    -DclassName="problem.HelloResource" \
    -Dpath="/hello" \
    -Dextensions="resteasy,resteasy-jackson"
cd quarkus-resteasy-problem-playground
./mvnw quarkus:add-extension -Dextensions="com.tietoevry.quarkus:quarkus-resteasy-problem:2.0.0"
```
Or add the following dependency to `pom.xml` in existing project:
```xml
<dependency>
    <groupId>com.tietoevry.quarkus</groupId>
    <artifactId>quarkus-resteasy-problem</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Quarkus 1.X / Java 1.8+
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
or
```xml
<dependency>
    <groupId>com.tietoevry.quarkus</groupId>
    <artifactId>quarkus-resteasy-problem</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Hint:** you can also use `resteasy-jsonb` or reactive equivalents: `resteasy-reactive-jackson` / `resteasy-reactive-jsonb` instead of `resteasy-jackson`


Once you run Quarkus: `./mvnw compile quarkus:dev`, and you will find `resteasy-problem` in the logs:
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

You may also want to check [this article](https://dzone.com/articles/when-http-status-codes-are-not-enough-tackling-web) on RFC7807 practical usage.  
More on throwing problems: [zalando/problem usage](https://github.com/zalando/problem#usage)

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
