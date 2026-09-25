---
name: babell-create-reusable-step
description: Add or extend shared BABELL API test behavior in the reusable Steps, API, or Helper layer. Use when a capability should be reusable; do not create a feature or scenario Test Definition.
---

# Create a reusable BABELL capability

Work only on reusable behavior below the Cucumber Test Definition. Read [the layer-responsibility guide](references/layer-responsibilities.md) and inspect existing implementations and callers before editing.

1. Identify the capability needed and search for an existing method that already provides it. Reuse or extend that behavior when possible instead of adding a duplicate.
2. Place new behavior in the lowest layer that owns it: `PokemonApiReusableSteps` for reusable test actions and domain assertions, `PokemonAPI` for Pokemon endpoint/domain operations, and `Helper` for generic HTTP transport and response utilities.
3. Keep Cucumber vocabulary, scenario IDs, feature files, and Test Definition bindings out of this task. Do not add a per-test Steps wrapper or move low-level behavior upward. Rest Assured stays behind `Helper`.
4. Add only the smallest reusable method or model change needed. Keep existing behavior intact and update callers only when required by the capability.
5. Compile and run `mvn clean test`; record the change using the repository's changelog convention. Report the chosen layer, reuse search, files changed, and validation result.
