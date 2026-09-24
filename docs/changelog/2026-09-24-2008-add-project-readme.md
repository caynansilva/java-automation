# 2026-09-24 20:08 - Add Project README

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Document the Java/Maven API automation project and the root-level commands to run its tests.

## Related Functionality
- Feature/module: project documentation
- Trigger: user request

## What Changed
- Added project overview, requirements, architecture, scenario list, dependencies, and Maven commands.
- Documented full-suite, clean-suite, and per-scenario test commands.
- Clarified that Maven commands are used directly without Node.js or `package.json`.

## Files Changed
| File | Change |
|------|--------|
| `README.md` | Added project guide and root-level Maven commands. |
| `docs/changelog/INDEX.md` | Added this changelog entry. |
| `docs/changelog/2026-09-24-2008-add-project-readme.md` | Recorded the documentation update. |

## Implementation Details
The README reflects the existing numbered Surefire include pattern and current test/Steps package layout. The repository has no Maven wrapper, so commands assume Maven is installed on PATH.

## Results
- Expected outcome: a new contributor can run all or one scenario from the project root.
- Actual outcome: README and changelog updated; documentation-only change was not test-validated.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Tests | Not run | skipped; documentation-only change |
| Lint | Not run | skipped |
| Manual | Reviewed documented commands against `pom.xml` and current scenario class names | pass |

## Behavior Impact
- No application or test behavior change expected.

## Risks and Follow-ups
- Maven must be installed and available on PATH; a Maven Wrapper is not included.
