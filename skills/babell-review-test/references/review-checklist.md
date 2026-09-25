# BABELL API Test Review Checklist

Review only the requested scope. Treat the current `docs/BABELL_CUCUMBER_MAVEN_ARCHITECTURE.md` and the actual Maven source layout as authoritative.

## Test ownership and discovery

- Each PK has one matching `.feature` and one Java Test Definition.
- A feature contains only its PK scenario and carries the matching `@PK_NNN` tag.
- The Test Definition package is `tests.pkNNN`; its glue value matches that package.
- Its `@SelectClasspathResource` selects only its matching feature.
- The Java class and feature follow `PK_NNN_API_TEST` naming and are discoverable by Surefire's `**/PK_*_API_TEST.java` include.
- No duplicate JUnit `@Test` scenario, per-PK `*_STEPS.java` file, global runner, global feature, or global Cucumber binding class exists.

## Layering and reuse

- Cucumber methods contain annotations, parameters, scenario vocabulary, and delegation to `PokemonApiReusableSteps`.
- Test Definitions contain no direct Rest Assured request construction, low-level response parsing, or copied reusable assertions.
- Existing reusable behavior is used before new methods are introduced.
- New behavior belongs in the correct shared layer: reusable test/domain actions in `PokemonApiReusableSteps`, Pokemon endpoint behavior in `PokemonAPI`, generic HTTP/response behavior in `Helper`.
- No cross-PK implementation dependency or unrelated scenario coupling is introduced.

## Report format

List findings by severity with exact file/line, the architectural impact, and a concrete correction. Distinguish verified defects from questions or maintainability suggestions. If the review passes, state that no findings were found and list the key checks performed. This is a report-only review; do not edit the repository.
