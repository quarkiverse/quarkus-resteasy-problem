# Problem Details for HTTP APIs (RFC-7807) implementation for Quarkus / RESTeasy.

[![Build status](https://github.com/TietoEVRY-DataPlatforms/quarkus-resteasy-problem/actions/workflows/maven-full.yaml/badge.svg)](https://github.com/TietoEVRY-DataPlatforms/quarkus-resteasy-problem/actions)
[![Release](https://img.shields.io/badge/release-v0.9.0-blue.svg)](https://github.com/TietoEVRY-DataPlatforms/quarkus-resteasy-problem/releases)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://raw.githubusercontent.com/TietoEVRY-DataPlatforms/quarkus-resteasy-problem/master/LICENSE.txt)
[![JVM](https://img.shields.io/badge/JVM-1.8+-blue.svg)](https://raw.githubusercontent.com/TietoEVRY-DataPlatforms/quarkus-resteasy-problem/master/LICENSE.txt)
![Features](https://img.shields.io/badge/features-jackson%20%7C%20jsonb%20%7C%20native%20mode-green)

[RFC7807 Problem](https://tools.ietf.org/html/rfc7807) extension for Quarkus RESTeasy/JaxRS applications, inspired and based on [Zalando Problem library](https://github.com/zalando/problem). \
This extension registers few JaxRS Exceptions Mappers for common exceptions thrown by Quarkus apps, which turn exceptions into standardized HTTP responses described in RFC, with content type `application/problem+json`. See [Built-in exception mappers](#built-in-exception-mappers) section for more details.

Supports:
- _resteasy-jackson_ and _resteasy-jsonb_
- JVM and native mode
- Java 8+
- Quarkus 1.4.2 +

## Table of contents
* [Why you should use it?](#why-you-should-use-it)
* [Usage](#usage)
* [Build-in exception mappers](#built-in-exception-mappers)
* [Configuration options](#configuration-options)

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
Add this to your pom.xml:
```xml
<dependency>
    <groupId>com.tietoevry.quarkus</groupId>
    <artifactId>quarkus-resteasy-problem</artifactId>
    <version>${resteasy-problem.version}</version>
</dependency>
```

Now you can throw JaxRS or custom exceptions (or Problems) from resources/controllers and business layer:

```java
import javax.ws.rs.*;

@Path("/test-endpoint")
@Produces(MediaType.APPLICATION_JSON)
public class ExampleResource {
    @GET
    public String fetchTestResource() {
        throw new NotFoundException("Test resource not found");
    }
}
```

Which will be translated to HTTP 404 response with body:
```json
HTTP/1.1 404 Not Found
Content-Length: 83
Content-Type: application/problem+json
        
{
  "title": "Not Found",
  "status": 404,
  "detail": "Test resource not found"
}
```

You'll also see this in the logs:
```
10:53:48 INFO [http-problem] (executor-thread-1) status=404, title="Not Found", detail="Test resource not found"
```
Exceptions transformed into http 500s (aka server errors) will be logged as `ERROR`, including full stacktrace.

More info on throwing problems from your code: [zalando/problem usage](https://github.com/zalando/problem#usage)

## Built-in exception mappers
This extension provides mappers for common exceptions thrown by Quarkus apps.\
Some of them are thrown by Quarkus itself in certain situations (i.e failed authorization).
You can throw them from controllers or business logic as well (i.e `NotFoundException` as in the example above).

| Exception                                | Thrown by / when                 | Example JSON response                           |
|------------------------------------------|--------------------------------|------------------------------------------------|
| `sec.AuthenticationFailedException`      | Missing or invalid JWT         | `{ "status" : 401, ... }`                      |
| `sec.UnauthorizedException`              | Missing or invalid JWT         | `{ "status" : 401, ... }`                      |
| `sec.ForbiddenException`                 | `@RolesAllowed` not satisfied  | `{ "status" : 403, ... }`                      |
| `javax.ConstraintViolationException`     | Hibernate Validator (`@Valid`) | `{ "status" : 400, violations : [{...}] }`     |
| `javax.ValidationException`              | user or Quarkus                | `{ "status" : 400, ... }`                      |
| `javax.RedirectionException`             | user or Quarkus                | `{ "status" : 3XX, ... }` + `Location` header    |
| `jaxrs.NotFoundException`                | RESTeasy, user                 | `{ "status" : 404, ... }`                      |
| `jaxrs.WebApplicationException(status)`  | user or Quarkus                | `{ "status" : <status>, ... }`                 |
| `zalando.Problem(status)`                | user or Quarkus                | `{ "status" : <status>, ... }`                 |
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
