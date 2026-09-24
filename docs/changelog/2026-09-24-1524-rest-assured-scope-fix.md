# 2026-09-24 15:24 - REST Assured Scope Fix

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Fix unresolved REST Assured symbols in the application API classes and make the API test exercise `PokemonAPI`.

## Related Functionality
- Feature/module: `src/main/java/com/caynan/qa/api` and `src/main/java/com/caynan/qa/utils`
- Trigger: user request

## What Changed
- Removed test-only scope from the REST Assured dependency so production source files can compile against it.
- Updated the Pikachu test to call `PokemonAPI.getPokemon`, making the API class and method used.

## Files Changed
| File | Change |
|------|--------|
| `pom.xml` | Made REST Assured available to main and test source sets. |
| `src/test/java/api/PokemonApiTest.java` | Routed the existing response assertions through `PokemonAPI`. |
| `docs/changelog/INDEX.md` | Added this correction to the index. |
| `docs/changelog/2026-09-24-1524-rest-assured-scope-fix.md` | Recorded the fix and validation. |

## Implementation Details
The `test` dependency scope had restricted REST Assured to `src/test/java`. `PokemonAPI` and `Helper` are under `src/main/java`, so Maven's main compilation could not see REST Assured types such as `Response` and `RequestSpecification`. Errors for `given`, `headers`, `body`, and HTTP methods followed from the missing dependency classes. The IDE's unused warnings appeared because the test had not called `PokemonAPI`.

## Results
- Expected outcome: main source compiles, the API class is exercised, and the Pikachu assertions pass.
- Actual outcome: Maven compiled main and test sources; one test passed.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Tests | `mvn test` | pass: 1 test, 0 failures, 0 errors |
| Lint | Not run | skipped |
| Manual | Public PokeAPI call from the JUnit test | pass |

## Behavior Impact
- The existing API test now calls the `PokemonAPI` class.

## Risks and Follow-ups
- The test requires network access to PokeAPI.
