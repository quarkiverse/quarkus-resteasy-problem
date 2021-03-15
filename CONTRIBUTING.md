# Contributing guide

You're more than welcome to work on this extension. Check [issues](../../issues/) first and pick something from there, 
or add your own suggestions and start discussion. Then create fork or branch and Pull Request once it's ready.

## Setup
- JDK 8

### IDE Config and Code Style

Quarkus has a strictly enforced code style, so as this extension. Code formatting is done by the Eclipse code formatter, using the config files
found in the `ide-config` directory. By default when you run `./mvnw install` the code will be formatted automatically.

If you want to run the formatting without doing a full build, you can run `./mvnw process-sources`.

More details on how to setup your ide can be found in official [Quarkus Contributing guide](https://github.com/quarkusio/quarkus/blob/main/CONTRIBUTING.md#ide-config-and-code-style)

### Modules
`runtime` - this happens in the runtime: this code ends up in your app's classpath\
`deployment` - this is Quarkus compile-time stuff\
`integration-test` - test scenarios + test endpoints + runners for Jackson and JsonB apps

### Running tests
Jackson profile is active by default when you use IntelliJ test runner.

Command line:\
`./mvnw test verify` - jackson profile enabled by default\ 
`./mvnw test verify -Pjsonb`\
`./mvnw test verify -Pjackson,quarkus-1.4` - checking backward compatibility with older versions of Quarkus

#### Native mode test
You need to have GraalVM and native-image installed to be able to run those tests.