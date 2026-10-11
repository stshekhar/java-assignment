# Java Code Assignment

This is a short code assignment that explores various aspects of software development, including API implementation, documentation, persistence layer handling, and testing.

## About the assignment

You will find the tasks of this assignment on [CODE_ASSIGNMENT](CODE_ASSIGNMENT.md) file

## About the code base

This is based on https://github.com/quarkusio/quarkus-quickstarts

### Requirements

To compile and run this demo you will need:

- JDK 17+

In addition, you will need either a PostgreSQL database, or Docker to run one.

### Configuring JDK 17+

Make sure that `JAVA_HOME` environment variables has been set, and that a JDK 17+ `java` command is on the path.

## Building the demo

Execute the Maven build on the root of the project:

```sh
./mvnw package
```

## Testing

Tests are under `src/test/java`. The suite includes:

- **Unit tests** for warehouse use cases, location lookup, and HTTP error-type mapping. Warehouse use-case tests use an in-memory `WarehouseStore` to exercise success paths and validation/not-found errors without starting Quarkus or connecting to a database.
- **HTTP endpoint tests** using `@QuarkusTest` and RestAssured for product and store requests. They exercise create/read/update/delete flows and representative invalid-request and not-found responses against the test application and database.
- **Integration tests** using `@QuarkusIntegrationTest`. `WarehouseEndpointIT` currently covers listing warehouses; the archive-flow test in that class is commented out, so that behavior is not currently covered through HTTP integration.

Run the regular unit and `@QuarkusTest` suite from this directory:

```powershell
.\mvnw.cmd test
```

On macOS/Linux, use `./mvnw test`. Ensure the database required by the test profile is available and configured as described in `src/main/resources/application.properties`.

Run a focused test class with:

```Windows
.\mvnw.cmd -Dtest=CreateWarehouseUseCaseTest test
```

When adding tests, cover both a representative successful operation and expected failure cases (status/error type or exception, plus relevant persisted state). Keep business-rule tests isolated as unit tests; use endpoint tests for HTTP serialization, routing, and status-code behavior. `WarehouseEndpointIT` is an integration-test class and is not run by the regular `test` goal; the `native` Maven profile configures the integration-test phase.

JaCoCo collects coverage from unit tests and Quarkus tests. Generate the report and enforce the 80% minimum line-coverage gate with:

```Windows
.\mvnw.cmd verify
```

The HTML report is written to `target/site/jacoco/index.html`; the machine-readable report is `target/site/jacoco/jacoco.xml`. Generated OpenAPI classes are excluded from the coverage denominator.

The current JaCoCo report measures **84% line coverage** across application classes (80% is the enforced minimum). Run `verify` in CI so coverage regressions fail the build.

Application errors are logged centrally by `ApiExceptionMapper`: expected client errors are logged without stack traces, while unexpected server failures include the exception and stack trace for diagnosis. Server error responses use a generic message rather than exposing internal exception details. The legacy store gateway uses the application logger and propagates synchronization failures rather than printing to stdout or silently swallowing them.

## Running the demo

### Live coding with Quarkus

The Maven Quarkus plugin provides a development mode that supports
live coding. To try this out:

```sh
./mvnw quarkus:dev
```

In this mode you can make changes to the code and have the changes immediately applied, by just refreshing your browser.

    Hot reload works even when modifying your JPA entities.
    Try it! Even the database schema will be updated on the fly.

## (Optional) Run Quarkus in JVM mode

When you're done iterating in developer mode, you can run the application as a conventional jar file.

First compile it:

```sh
./mvnw package
```

Next we need to make sure you have a PostgreSQL instance running (Quarkus automatically starts one for dev and test mode). To set up a PostgreSQL database with Docker:

```sh
docker run -it --rm=true --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 15432:5432 postgres:13.3
```

Connection properties for the Agroal datasource are defined in the standard Quarkus configuration file,
`src/main/resources/application.properties`.

Then run it:

```sh
java -jar ./target/quarkus-app/quarkus-run.jar
```
    Have a look at how fast it boots.
    Or measure total native memory consumption...


## See the demo in your browser

Navigate to:

<http://localhost:8080/index.html>

Have fun, and join the team of contributors!

## Troubleshooting

Using **IntelliJ**, in case the generated code is not recognized and you have compilation failures, you may need to add `target/.../jaxrs` folder as "generated sources".