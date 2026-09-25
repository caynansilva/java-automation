# 2026-09-25 09:59 - Add BABELL Agent Skills

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Make the BABELL API test architecture usable as focused, discoverable instructions for coding agents.

## Related Functionality
- Feature/module: repository-local agent guidance for BABELL API testing
- Trigger: user request

## What Changed
- Added three focused skills for creating API tests, adding reusable behavior, and reviewing tests.
- Added a focused reference to each skill and Codex UI metadata with automatic invocation left enabled.
- Documented the skills and a minimal user prompt in README.
- Marked the older Cucumber decision record as superseded and linked the current Maven architecture guide.

## Files Changed
| File | Change |
|------|--------|
| `skills/babell-create-api-test/SKILL.md` | Added test-creation workflow and guardrails. |
| `skills/babell-create-api-test/agents/openai.yaml` | Added skill discovery metadata. |
| `skills/babell-create-api-test/references/architecture.md` | Added current PK structure and example. |
| `skills/babell-create-reusable-step/SKILL.md` | Added shared-capability workflow and scope. |
| `skills/babell-create-reusable-step/agents/openai.yaml` | Added skill discovery metadata. |
| `skills/babell-create-reusable-step/references/layer-responsibilities.md` | Documented reusable-layer ownership. |
| `skills/babell-review-test/SKILL.md` | Added report-only review workflow. |
| `skills/babell-review-test/agents/openai.yaml` | Added skill discovery metadata. |
| `skills/babell-review-test/references/review-checklist.md` | Added architecture review checklist. |
| `README.md` | Added AI skill overview, links, architecture rule, and example prompt. |
| `docs/BABELL_CUCUMBER_DECISION.md` | Marked the earlier three-artifact decision as superseded. |
| `docs/changelog/INDEX.md` | Indexed this entry. |
| `docs/changelog/2026-09-25-0959-add-babell-agent-skills.md` | Recorded implementation and validation. |

## Implementation Details
The creation skill establishes one feature plus one `tests.pkNNN` Test Definition and directs missing shared behavior to the reusable-layer workflow. The reusable-step skill never creates scenarios. The review skill is explicitly read-only. Details live in references instead of being repeated across all three entrypoints.

## Results
- Expected outcome: agents can create, extend, or review BABELL tests using the repository's current architecture.
- Actual outcome: all skills passed structural validation; a temporary PK_008 test reused the existing Pokemon ability assertion and passed targeted and full-suite execution.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Skill structure | `quick_validate.py` on all three skill directories | pass |
| Creation workflow | Temporary PK_008 Bulbasaur `overgrow` test; targeted Maven run | pass; 1 scenario |
| Complete test workflow | Temporary PK_008 project copy `mvn clean test` | pass; 8 scenarios, 0 failures |
| Reusable-step guidance | Checked placement/reuse for the existing Pokemon ability assertion | pass; existing `PokemonApiReusableSteps.assertPokemonHasAbility` was reused |
| Review guidance | Ran the checklist against PK_001–PK_007 structure and bindings | pass; all checks satisfied |
| Repository isolation | Inspected repository source and Maven files after temporary validation | pass; no PK_008 artifacts added to the repository |

## Behavior Impact
- No Java application or Maven test behavior changed. Skills add agent guidance and README documentation.

## Risks and Follow-ups
- Test-creation skill validation uses the live PokeAPI and requires network availability.
