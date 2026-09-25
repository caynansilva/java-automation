# 2026-09-25 10:40 - Add Java Gherkin Generator

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Add BABELL-native Java G2F generation from one feature into its matching Cucumber/JUnit Test Definition.

## Related Functionality
- Feature/module: `com.caynan.qa.tools.GherkinToFunctions`
- Trigger: user request

## What Changed
- Added a Java Gherkin parser and Test Definition generator with safe output handling.
- Added focused tests for syntax conversion, validation, overwrite handling, and recursive input.
- Added Windows and POSIX launchers and documented G2F in the README.

## Files Changed
| File | Change |
|------|--------|
| `pom.xml` | Add the Gherkin parser, exec launcher configuration, and test discovery. |
| `src/main/java/com/caynan/qa/tools/GherkinToFunctions.java` | Parse supported Gherkin and generate the PK-specific suite class. |
| `src/test/java/tools/g2f/GherkinToFunctionsTest.java` | Add focused generator tests. |
| `g2f.cmd`, `g2f` | Add Maven-backed Windows and POSIX command wrappers. |
| `README.md` | Explain the G2F pipeline, commands, limits, and force behavior. |
| `docs/changelog/INDEX.md` | Index this entry. |

## Implementation Details
- Gherkin parser version is 28.0.0, matching the dependency resolved by the existing Cucumber 7.20.1 integration.
- Generated binding bodies throw `UnsupportedOperationException` pending explicit BABELL delegation.
- Existing Test Definitions are preserved unless `--force` is requested.

## Results
- Expected outcome: generate one Java Test Definition per valid BABELL feature.
- Actual outcome: 22 G2F tests passed; the seven existing BABELL scenarios passed; the generated PK_008 class compiled during CLI smoke validation.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Generator tests | `mvn -Dtest=GherkinToFunctionsTest test` | 22 passed |
| BABELL regression | `mvn clean test` | 29 total passed (22 generator tests + 7 Cucumber scenarios) |
| Generated source | Windows and POSIX launcher directory smoke test; generated PK_008 compiled with `mvn test-compile` | pass |
| Diff check | `git diff --check` | pass |

## Behavior Impact
- Adds project-local G2F commands; existing PK_001–PK_007 feature behavior is intended to remain unchanged.

## Risks and Follow-ups
- Maven was not on `PATH`; validation used the checksum-verified Apache Maven 3.9.16 distribution in a temporary directory.
