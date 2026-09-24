# 2026-09-24 20:03 - Expand PokeAPI Scenarios

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Expand the PokeAPI BABELL suite with six scenarios while keeping tests readable and sharing reusable test operations.

## Related Functionality
- Feature/module: `src/test/java/api` and `src/test/java/test_steps`
- Trigger: user request

## What Changed
- Added PK_002 through PK_007 for lookup by ID, not found, response contract, types, abilities, and response metadata.
- Added `PokemonApiReusableSteps` for shared requests and typed response assertions.
- Updated PK_001 steps to delegate to the shared reusable operations.
- Configured Surefire to discover the repository's `PK_*_API_TEST` naming convention.
- Added no Cucumber dependency.

## Files Changed
| File | Change |
|------|--------|
| `pom.xml` | Configured Surefire to discover numbered API test classes. |
| `src/test/java/test_steps/PokemonApiReusableSteps.java` | Added reusable request, response, contract, type, ability, status, and metadata operations. |
| `src/test/java/test_steps/PK_001_API_TEST_STEPS.java` | Delegated the existing scenario to shared operations. |
| `src/test/java/api/PK_002_API_TEST.java` | Added Pokémon-by-ID scenario. |
| `src/test/java/test_steps/PK_002_API_TEST_STEPS.java` | Added ID scenario BDD steps. |
| `src/test/java/api/PK_003_API_TEST.java` | Added not-found scenario. |
| `src/test/java/test_steps/PK_003_API_TEST_STEPS.java` | Added not-found BDD steps. |
| `src/test/java/api/PK_004_API_TEST.java` | Added Pokémon response contract scenario. |
| `src/test/java/test_steps/PK_004_API_TEST_STEPS.java` | Added response contract BDD steps. |
| `src/test/java/api/PK_005_API_TEST.java` | Added Bulbasaur type scenario. |
| `src/test/java/test_steps/PK_005_API_TEST_STEPS.java` | Added reusable type assertion BDD steps. |
| `src/test/java/api/PK_006_API_TEST.java` | Added Pokémon ability scenario. |
| `src/test/java/test_steps/PK_006_API_TEST_STEPS.java` | Added ability assertion BDD steps. |
| `src/test/java/api/PK_007_API_TEST.java` | Added response metadata scenario. |
| `src/test/java/test_steps/PK_007_API_TEST_STEPS.java` | Added content-type BDD steps. |
| `docs/changelog/INDEX.md` | Added this changelog entry. |
| `docs/changelog/2026-09-24-2003-expand-pokeapi-scenarios.md` | Recorded the implementation and actual test result. |

## Implementation Details
All numbered test classes use only JUnit and BDD step calls. Rest Assured remains behind `PokemonAPI`/`Helper`; the reusable step class holds response state and parses successful responses into `PokemonResponse`. The PK_003 flow checks status 404 without deserializing that response. Surefire's default filename patterns did not match `PK_001_API_TEST`, so an explicit include pattern was necessary for Maven to run the suite.

## Results
- Expected outcome: seven numbered scenarios execute through the BABELL layers.
- Actual outcome: all seven tests passed, with zero failures or errors.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Tests | `mvn test` | pass: 7 tests, 0 failures, 0 errors, 0 skipped |
| Rest Assured isolation | `rg -n "io\\.restassured|RestAssured|given\\(" src/test/java` | pass: Rest Assured `Response` type appears only in the reusable Steps class; request builders are not used in tests. |
| Manual | Live PokeAPI calls exercised by the suite | pass |

## Behavior Impact
- Maven now executes all numbered `PK_*_API_TEST` classes; previously, the default Surefire includes did not match those class names.

## Risks and Follow-ups
- The scenarios require the public PokeAPI to be reachable and available.
