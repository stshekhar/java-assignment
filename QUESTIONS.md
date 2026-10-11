# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes, I would gradually make the persistence approach more consistent. Products use a Panache repository, Stores use Panache's active-record style directly from the REST resource, while Warehouses have a domain-facing store interface and a separate database adapter. Each approach can work, but having all three makes it harder to know where persistence logic and transaction boundaries belong, and it makes testing and future changes less predictable.

I would keep the warehouse port-and-adapter structure for business operations and move Store and Product persistence behind repositories and application services/use cases as those areas are changed. That would leave REST resources focused on HTTP concerns and give the application layer a clear place for business rules and transactions. Active Record is still reasonable for very simple CRUD, so I would not rewrite everything just for uniformity; I would migrate incrementally and keep the extra abstraction where it provides a real benefit.

I would also review the warehouse repository's business-unit-code lookup: it currently scans all rows in memory and has a fallback that can return archived records. A database-backed query, supported by an appropriate index and explicit active-versus-archived behavior, would be clearer and more efficient. I would protect these changes with repository and use-case tests, then migrate one area at a time to avoid a risky, all-at-once rewrite.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
I see benefits to both approaches. Generating the Warehouse API from an OpenAPI spec gives us a clear contract that can be reviewed, documented, and used to generate client code. It also helps keep endpoint shapes consistent. The tradeoff is extra tooling and another artifact to maintain; the spec can drift from the handwritten implementation, and generated code can be awkward to customize. In this project, the interface and model are generated, but `WarehouseResourceImpl` still contains the actual mapping and endpoint behavior.

Writing Product and Store endpoints directly in Java is straightforward and flexible, which can be a good fit for a small service. The downside is that the contract is less visible and can be harder to keep in sync with documentation or client expectations.

For this codebase, I would gradually add OpenAPI specs for Product and Store too, especially if other teams or clients depend on these APIs. I would keep generated files separate from handwritten endpoint logic, and treat the spec as the contract that tests and implementation should follow. If an endpoint remains purely internal and simple, direct implementation may be enough; I would avoid adding generation overhead without a clear benefit.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I would start with the parts where a bug would be most costly: warehouse business rules and the create/update/delete flows for each API. I’d keep fast JUnit tests around isolated rules, then use Quarkus endpoint tests to check that requests, responses, status codes, validation, and persistence work together. For database adapters, I’d add focused tests for queries and mapping; broader integration tests are useful for verifying the application against its configured runtime and database.

For each important operation, I’d cover a normal success case as well as likely failure cases—for example, a missing record, invalid input, or a warehouse capacity/location constraint. I’d check not only the returned error but also that failed operations leave data unchanged. This gives more confidence than testing only the happy path.

To keep the suite useful over time, I’d run it in CI, keep tests isolated and deterministic, and add regression tests whenever a defect is fixed. JaCoCo provides a helpful signal and this project enforces an 80% line-coverage minimum on `mvnw verify`; I’d use that as a floor, not as the goal by itself. I’d review the report for important untested behavior and keep the integration tests active as well as the faster unit and endpoint tests.
```