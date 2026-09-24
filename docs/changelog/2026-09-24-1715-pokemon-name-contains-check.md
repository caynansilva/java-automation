# 2026-09-24 17:15 - Pokemon Name Contains Check

## Status
- [ ] Pending review
- [x] Implemented

## Objective
Implement `PokemonAPI.assertPokemonNameContains` using the typed `PokemonResponse` model.

## Related Functionality
- Feature/module: `src/main/java/com/caynan/qa/api/PokemonAPI.java`
- Trigger: user request

## What Changed
- The method validates its input, requests that Pokémon, returns false for non-200 responses, deserializes the response into `PokemonResponse`, and checks whether the response name contains the requested string.

## Files Changed
| File | Change |
|------|--------|
| `src/main/java/com/caynan/qa/api/PokemonAPI.java` | Replaced the constant true result with a response-backed name check. |
| `docs/changelog/INDEX.md` | Added this entry to the index. |
| `docs/changelog/2026-09-24-1715-pokemon-name-contains-check.md` | Recorded the implementation and compile result. |

## Implementation Details
The method uses `Helper.parseResponse(response, PokemonResponse.class)` and the record's `name()` accessor. Invalid or blank input and non-success status codes return false.

## Results
- Expected outcome: the method returns whether the Pokémon response name contains the supplied name.
- Actual outcome: main source compilation passed.

## Validation
| Check | Command / action | Result |
|-------|------------------|--------|
| Tests | Not run | skipped |
| Compile | `mvn compile` | pass |
| Manual | Not performed | skipped |

## Behavior Impact
- The method now makes an HTTP request and evaluates the typed response instead of always returning true.

## Risks and Follow-ups
- Since the argument is also used as the Pokémon endpoint identifier, it should be a full Pokémon name or ID; arbitrary substrings will usually receive a not-found response and return false.
