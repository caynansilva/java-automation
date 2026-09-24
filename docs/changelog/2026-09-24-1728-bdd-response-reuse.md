# 2026-09-24 17:28 - BDD Response Reuse

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Structure the Pikachu API test as reusable BDD-style steps that share one response.

## Related Functionality
- Feature/module: `src/test/java/api/PokemonApiTest.java`
- Trigger: user request

## What Changed
- Moved the Given, Then, and And steps inside the JUnit test class.
- Stored the HTTP response in a class field shared by the step methods for that test instance.
- Asserted the Boolean result of the typed Pokemon name check.
- Added response and input guards to avoid trying to deserialize unsuccessful responses.

## Files Changed
| File | Change |
|------|--------|
| `src/test/java/api/PokemonApiTest.java` | Added BDD-style test methods and shared response state. |
| `src/main/java/com/caynan/qa/api/PokemonAPI.java` | Validated the captured response as `PokemonResponse`. |
| `docs/changelog/INDEX.md` | Added this entry to the index. |
| `docs/changelog/2026-09-24-1728-bdd-response-reuse.md` | Recorded the implementation and test result. |

## Implementation Details
The response is an instance field rather than a static global. JUnit's default per-method test instance keeps the stored response available to all steps in one test without sharing it between different test executions. The test uses `assertTrue` so a false return from `assertPokemonNameContains` fails the test.

## Results
- Expected outcome: one request is sent, the response is reused by the assertion steps, and both checks pass.
- Actual outcome: one test passed.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Tests | `mvn test` | pass: 1 test, 0 failures, 0 errors |
| Lint | Not run | skipped |
| Manual | Live PokeAPI request through test | pass |

## Behavior Impact
- The response from the Given step is reused by the Then and And steps; no duplicate API request is made.

## Risks and Follow-ups
- The test depends on network access to PokeAPI.
