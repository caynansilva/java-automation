# 2026-09-24 17:06 - Complete Pokemon Response Model

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Map the complete documented PokeAPI Pokémon response, including nested sprite and move data.

## Related Functionality
- Feature/module: `src/main/java/com/caynan/qa/types/PokemonResponse.java`
- Trigger: user request

## What Changed
- Added the remaining standard and nested sprite URL fields.
- Modeled dynamic sprite generation/game entries as typed maps and included nested animations.
- Added move version detail `order`.
- Added Jackson databind so the model annotations compile and the record can be deserialized.

## Files Changed
| File | Change |
|------|--------|
| `src/main/java/com/caynan/qa/types/PokemonResponse.java` | Completed documented Pokémon response mapping. |
| `pom.xml` | Added Jackson databind dependency. |
| `docs/changelog/INDEX.md` | Added this entry to the index. |
| `docs/changelog/2026-09-24-1706-complete-pokemon-response-model.md` | Recorded changes and compilation result. |

## Implementation Details
PokeAPI generation and game names are object keys under `sprites.versions`, so these objects use nested maps rather than hard-coding every generation and title. `PokemonGameSprites` contains shared URL fields and recursively models the optional `animated` sprites object. JSON property annotations map snake_case names to Java camelCase record components.

## Results
- Expected outcome: represent the documented Pokémon endpoint response with typed Java records.
- Actual outcome: main sources compile successfully.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Tests | Not run | skipped |
| Compile | `mvn compile` | pass |
| Manual | Compared model fields with PokeAPI's documented example response | pass |

## Behavior Impact
- The response model now retains structured sprite variants, game version details, and move ordering.

## Risks and Follow-ups
- `@JsonIgnoreProperties(ignoreUnknown = true)` intentionally tolerates future fields; new properties are not typed until added to the model.
