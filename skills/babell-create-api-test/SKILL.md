---
name: babell-create-api-test
description: Create an isolated BABELL API test from a requirement using the repository's Cucumber and JUnit Platform architecture. Use for new API scenarios; do not use for standalone reusable-layer changes or reviews.
---

# Create a BABELL API test

Create one independent test unit from the user's behavior requirement. Read [the architecture reference](references/architecture.md) before editing.

1. Inspect the existing feature files and Test Definitions to find the next unused `PK_NNN` identifier. Inspect `PokemonApiReusableSteps`, `PokemonAPI`, `Helper`, and response types for reusable behavior before proposing code.
2. Create exactly one feature at `src/test/resources/features/PK_NNN_API_TEST.feature` and one Test Definition at `src/test/java/tests/pknnn/PK_NNN_API_TEST.java`. Keep one scenario in the feature. The Java package and configured Cucumber glue must both be `tests.pknnn`.
3. Put the JUnit Platform Suite metadata and only this test's Cucumber bindings in the Test Definition. Bindings should translate Gherkin phrases into calls on `PokemonApiReusableSteps`.
4. Never create a per-test `*_STEPS.java`, a global Cucumber binding class, a shared feature for unrelated tests, or direct Rest Assured calls in a Test Definition. Do not duplicate an existing assertion, request, parsing, or API capability.
5. If the requested behavior needs reusable functionality that does not exist, follow [the reusable-step skill](../babell-create-reusable-step/SKILL.md) and its [layer guide](../babell-create-reusable-step/references/layer-responsibilities.md). Keep that implementation out of the Test Definition.
6. Add the new scenario to an existing project coverage list or changelog when those conventions are present. Run `mvn test -Dtest=PK_NNN_API_TEST`, then `mvn clean test`; report both results and any external API/network failure.

If the requirement does not define observable behavior clearly enough to write meaningful Given/Then steps, ask for the missing behavior before inventing expectations.
