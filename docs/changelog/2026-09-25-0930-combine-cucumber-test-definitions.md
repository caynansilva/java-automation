# 2026-09-25 09:30 - Combine Cucumber Test Definitions

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Combine each PK's Cucumber suite metadata and scenario bindings into one isolated Java Test Definition, keeping shared behavior in `PokemonApiReusableSteps`.

## Related Functionality
- Feature/module: Java 21 Maven Cucumber API test suite
- Trigger: user request

## What Changed
- Moved PK_001 through PK_007 test definitions into the isolated `tests.pk001` through `tests.pk007` packages.
- Moved each scenario's Cucumber bindings into its matching suite class and configured that package as its glue.
- Removed all seven standalone `PK_xxx_API_TEST_STEPS.java` files.
- Kept the seven independent feature files and reusable BABELL steps.
- Removed a stale `docs` Maven module declaration because no `docs/pom.xml` existed and it prevented Maven test discovery.
- Updated the README with the one-feature/one-Test-Definition layout.

## Files Changed
| File | Change |
|------|--------|
| `src/test/java/tests/pk001/PK_001_API_TEST.java` through `pk007/PK_007_API_TEST.java` | Combined each runner's JUnit Platform metadata with its Cucumber bindings. |
| `src/test/java/tests/PK_001_API_TEST.java` through `PK_007_API_TEST.java` | Replaced the old flat-package suite locations. |
| `src/test/java/steps/cucumber/pk001/PK_001_API_TEST_STEPS.java` through `pk007/PK_007_API_TEST_STEPS.java` | Removed redundant per-scenario bindings. |
| `pom.xml` | Removed the invalid `docs` child module declaration; retained JUnit Platform, Cucumber, and Surefire configuration. |
| `README.md` | Documented the combined Test Definition architecture. |
| `docs/BABELL_CUCUMBER_MAVEN_ARCHITECTURE.md` | Updated the isolated glue example to use `tests.pk001`. |
| `docs/changelog/INDEX.md` | Added an index for this entry; pre-existing deleted changelog files were not restored. |
| `docs/changelog/2026-09-25-0930-combine-cucumber-test-definitions.md` | Recorded implementation and validation. |

## Implementation Details
PK_001 was first migrated and executed independently to verify that Cucumber 7.20.1 discovers step bindings from the same class used as a JUnit Platform Suite. After that passed, the same pattern was applied to the remaining six scenarios. The root POM initially failed before compilation because it referenced a nonexistent `docs/pom.xml`; restoring the root as a single Maven project allowed the requested test lifecycle to run.

## Results
- Expected outcome: exactly one isolated Java Test Definition per PK with Cucumber metadata and bindings, delegating behavior to reusable BABELL steps.
- Actual outcome: the combined-class approach passed PK_001 independently and all seven scenarios in the full suite.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Combined suite/glue proof | `mvn test -Dtest=PK_001_API_TEST` after moving PK_001 into `tests.pk001` | pass; one scenario, no undefined or ambiguous steps |
| Full suite | `mvn clean test` | pass; 7 run, 0 failures, 0 errors, 0 skipped |
| Structural review | Checked feature, Test Definition, binding, and reusable-steps locations | pass; one feature and one Test Definition per PK; no separate PK bindings remain |

## Behavior Impact
- PK scenarios now execute through one Java class per PK, while API behavior and assertions remain in shared reusable steps.

## Risks and Follow-ups
- Tests call the live PokeAPI and require network access.
