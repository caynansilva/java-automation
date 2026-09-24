# 2026-09-24 17:33 - Extract API Test Steps

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Move BDD step implementations into `PK_001_API_TEST_STEPS` and keep the JUnit test focused on the scenario flow.

## Related Functionality
- Feature/module: `src/test/java/api`
- Trigger: user request

## What Changed
- Added `PK_001_API_TEST_STEPS` to hold the response and implement the Given, Then, and And steps.
- Reduced `PK_001_API_TEST` to the JUnit test method and its BDD step calls.

## Files Changed
| File | Change |
|------|--------|
| `src/test/java/api/PK_001_API_TEST_STEPS.java` | Added reusable BDD step implementations and response state. |
| `src/test/java/api/PokemonApiTest.java` | Kept only the scenario method and its step calls. |
| `docs/changelog/INDEX.md` | Added this entry to the index. |
| `docs/changelog/2026-09-24-1733-extract-api-test-steps.md` | Recorded the change and validation. |

## Implementation Details
The steps class is in the `api` test package. One instance is held by the test class, and its `Response` field is reused by subsequent steps. No Cucumber dependency or runner was added.

## Results
- Expected outcome: the test reads like a compact BDD scenario while step code remains reusable.
- Actual outcome: one JUnit test passed.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Tests | `mvn test` | pass: 1 test, 0 failures, 0 errors |
| Lint | Not run | skipped |
| Manual | Live PokeAPI request through test | pass |

## Behavior Impact
- Test behavior remains the same; step implementations now live in a dedicated class.

## Risks and Follow-ups
- The test depends on network access to PokeAPI.
