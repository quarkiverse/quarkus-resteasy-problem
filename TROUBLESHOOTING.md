# Troubleshooting
List of common problems you may encounter when using this extension.

## WebApplicationExceptions are not handled properly sometimes
Long story short: if your application throws WebApplicationException, which wraps Response object, which includes entity (body), this exception will **bypass all JaxRS Exception Mappers!**. API client will see exactly the Response wrapped by this exception.

I.e this happens when you use RestClient without assigning ExceptionMapper (via @RegisterProvider). The result is that all WebApplicationExceptions (wrapping Response object that includes entity) thrown by RestClient will be propagated 'as-is' to your application's api clients directly without any modification. 

This exception:
```java
throw new WebApplicationException(Response.status(400).entity("{\"message\": \"This request is bad\"}").build());
```
will bypass all matching Exception Mappers, and http response will look like this (with http status 400):
```json
{
  "message": "This request is bad"
}
```

On the other hand, if your WebApplicationException does not wrap Response object, or Response does not have an entity (body):
```java
throw new WebApplicationException(400);
// or
throw new WebApplicationException(Response.status(400).build());
```
Exception Mapper will be triggered as expected, and the response will look like this:
```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "HTTP 400 Bad Request"
}
```

### Explanation
This is all by-design: [JaxRS specification](https://raw.githubusercontent.com/javaee/jax-rs-spec/master/spec.pdf) defines how WebApplicationException should be translated into http response, which all implementations must follow (including RESTeasy).

```
Section 3.3.4
(...)
Instances of WebApplicationException and its subclasses MUST be mapped to a response as follows. If the response property of the exception does not contain an entity and an exception mapping
provider (see Section 4.4) is available for WebApplicationException or the corresponding subclass, an implementation MUST use the provider to create a new Response instance, otherwise the
response property is used directly. 
(...)
```

### References  
[JaxRS specification](https://raw.githubusercontent.com/javaee/jax-rs-spec/master/spec.pdf)
[Quarkus issue related to this behaviour](https://github.com/quarkusio/quarkus/issues/4031)