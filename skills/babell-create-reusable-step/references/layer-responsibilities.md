# BABELL Reusable Layer Responsibilities

Inspect the current code and callers first. This guide describes ownership, not permission to duplicate existing behavior.

| Layer | Owns | Examples |
|---|---|---|
| `PokemonApiReusableSteps` | Reusable test actions, scenario response state, and Pokemon-specific assertions | Request Pokemon, assert a response has a Pokemon type or ability, assert response status through Helper |
| `PokemonAPI` | Pokemon endpoint operations and Pokemon API-specific response behavior | Build the Pokemon resource URL and request a Pokemon; interpret Pokemon response data |
| `Helper` | Generic HTTP transport and response utilities | Execute HTTP verbs with Rest Assured, inspect headers/status/content type, parse a response to a requested type |

## Placement decisions

- A request for `GET /pokemon/{name}` belongs to `PokemonAPI`; generic HTTP execution remains in `Helper`.
- A Pokemon domain assertion such as “has type grass” belongs in `PokemonApiReusableSteps`, using the existing response model and reusable parsing path.
- A domain-neutral response operation such as checking a header exists or parsing a response belongs in `Helper` when it is not already available.
- Scenario vocabulary, Cucumber annotations, tags, feature files, and suite metadata do not belong in this skill's scope.

Do not put Rest Assured calls, raw HTTP request construction, response deserialization details, or copied assertions in Cucumber Test Definitions. Do not add a reusable method merely to wrap an existing method without adding a meaningful capability.

## Change and validation

Add the smallest API needed by actual callers. Preserve existing semantics, inspect all call sites, and compile/run `mvn clean test`. Add test coverage only when the repository's test layers can exercise the reusable behavior without creating a new Cucumber scenario as part of this task.
