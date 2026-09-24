# 2026-09-24 14:33 - Simple PokeAPI Test

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Create a minimal Maven and Java API automation example that calls PokeAPI and verifies Pikachu's response.

## Related Functionality
- Feature/module: `src/test/java/api`
- Trigger: user request

## What Changed
- Configured Java 21, JUnit 5, and REST Assured in Maven.
- Added one Given / When / Then test for the Pikachu endpoint.

## Files Changed
| File | Change |
|------|--------|
| `pom.xml` | Added test dependencies and compiler/Surefire plugin configuration. |
| `src/test/java/api/PokemonApiTest.java` | Added status, name, and id assertions for Pikachu. |
| `docs/changelog/INDEX.md` | Added this change to the changelog index. |
| `docs/changelog/2026-09-24-1433-simple-pokeapi-test.md` | Recorded the implementation and validation. |

## Implementation Details
The JUnit Jupiter test uses REST Assured's `given()`, `when()`, and `then()` syntax. Maven Surefire 3.5.2 enables JUnit 5 test discovery.

## Results
- Expected outcome: `mvn test` runs the API test.
- Actual outcome: one test passed against PokeAPI.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Tests | `mvn test` | pass: 1 test, 0 failures, 0 errors |
| Lint | Not run | skipped |
| Manual | Called the public PokeAPI endpoint through the test | pass |

## Behavior Impact
- Developers can run a basic API check through Maven.

## Risks and Follow-ups
- The test requires network access to PokeAPI.
