# PokeAPI Java API Automation

A Java 21 and Maven API automation project for the public [PokeAPI](https://pokeapi.co/). Each scenario is defined in its own Gherkin feature and executed by a dedicated Cucumber JUnit Platform suite. Rest Assured remains behind the BABELL API and `Helper` layers.

## Requirements

- JDK 21
- Apache Maven available as `mvn` on your command line
- Internet access to reach PokeAPI

Confirm Java and Maven are available from the project root:

```shell
java -version
mvn -version
```

## Run the scenarios

Run the complete suite:

```shell
mvn clean test
```

Run one PK scenario by its runner class:

```shell
mvn test -Dtest=PK_005_API_TEST
```

Or select a scenario by its Cucumber tag:

```shell
mvn test -Dcucumber.filter.tags="@PK_005"
```

## Scenario structure

Each PK has exactly one feature file and one isolated Java Test Definition:

```text
src/test/resources/features/PK_001_API_TEST.feature
src/test/java/tests/pk001/PK_001_API_TEST.java
src/test/java/steps/PokemonApiReusableSteps.java
```

The same layout applies to PK_002 through PK_007. Each feature contains only its corresponding scenario. Each Test Definition combines JUnit Platform suite metadata with that PK's Cucumber bindings, and selects only its feature and matching `tests.pkNNN` glue package. Similar Gherkin phrases can be reused without loading other tests' bindings.

The Test Definition contains only scenario vocabulary and delegates to `PokemonApiReusableSteps`. That reusable BABELL layer owns response state and assertions, calls `PokemonAPI`, then uses `Helper` for HTTP handling. Rest Assured remains in `Helper`.

```text
PK feature → PK-specific Test Definition → PokemonApiReusableSteps → PokemonAPI → Helper → Rest Assured
```

## Gherkin to Functions (G2F)

G2F generates one PK-specific Java Test Definition from each existing feature:

```text
.feature
   ↓
G2F
   ↓
PK_xxx_API_TEST.java
   ↓
Reusable Steps
   ↓
BABELL
```

Run a single feature, or recursively process a directory:

```shell
# Windows PowerShell
.\g2f.cmd src/test/resources/features/PK_008_API_TEST.feature
.\g2f.cmd --dir src/test/resources/features

# Linux / macOS
sh ./g2f src/test/resources/features/PK_008_API_TEST.feature
sh ./g2f --dir src/test/resources/features
```

To deliberately regenerate an existing Test Definition, pass `--force`:

```shell
.\g2f.cmd --force src/test/resources/features/PK_008_API_TEST.feature
sh ./g2f --force src/test/resources/features/PK_008_API_TEST.feature
```

`--force` replaces the Java file and can destroy manually implemented BABELL delegations. By default, G2F refuses to overwrite an existing Test Definition. Generated bindings throw `UnsupportedOperationException` until connected to reusable BABELL behavior; G2F does not invent test implementation. Supported syntax includes Feature/tags, Background, one Scenario, and Given/When/Then/And/But. Unsupported constructs such as Scenario Outline, Examples, Data Tables, Doc Strings, and Rule are reported with guidance instead of being ignored.

One Test, One Feature, One Test Definition. G2F creates no per-test step classes, global glue, or Rest Assured code.

Surefire discovers the seven `PK_*_API_TEST` suite classes in `tests.pk001` through `tests.pk007`. They are Cucumber Test Definitions, not JUnit `@Test` scenario copies. `mvn test -Dtest=PK_005_API_TEST` therefore runs only PK_005.

## Using BABELL with AI Coding Agents

This repository includes small Agent Skills that teach supported coding agents how to work within the BABELL architecture:

| Skill | Purpose |
|---|---|
| [`babell-create-api-test`](skills/babell-create-api-test/SKILL.md) | Create a complete API test from a behavior requirement. |
| [`babell-create-reusable-step`](skills/babell-create-reusable-step/SKILL.md) | Add shared behavior at the correct reusable layer. |
| [`babell-review-test`](skills/babell-review-test/SKILL.md) | Review tests for architecture and maintainability without editing them. |

For example, ask:

> Create a BABELL API test that verifies Bulbasaur has the ability "overgrow".

The test-creation skill inspects existing reusable capabilities and creates one feature and one Test Definition, such as `PK_008_API_TEST.feature` and `tests/pk008/PK_008_API_TEST.java`.

### Agent architecture rule

One Test, One Feature, One Test Definition. Shared behavior belongs below the Test Definition. Agents must not create global Step Definition classes, per-test `*_STEPS.java` files, multiple unrelated scenarios in one feature, direct Rest Assured calls in Test Definitions, or duplicate behavior when a reusable BABELL capability already exists.

## Scenario coverage

| Runner / tag | Coverage |
|--------------|----------|
| `PK_001_API_TEST` / `@PK_001` | Request Pikachu by name; check name and HTTP 200. |
| `PK_002_API_TEST` / `@PK_002` | Request Pokémon ID 25; check Pikachu, ID 25, and HTTP 200. |
| `PK_003_API_TEST` / `@PK_003` | Request an unknown Pokémon; check HTTP 404. |
| `PK_004_API_TEST` / `@PK_004` | Check the Pokémon response contract. |
| `PK_005_API_TEST` / `@PK_005` | Check Bulbasaur has grass and poison types. |
| `PK_006_API_TEST` / `@PK_006` | Check Pikachu has the static ability. |
| `PK_007_API_TEST` / `@PK_007` | Check HTTP 200 and JSON content type. |

## Dependencies

Maven declares Java 21, JUnit Jupiter 5, the JUnit Platform Suite, Cucumber Java, Cucumber's JUnit Platform engine, REST Assured, and Jackson Databind in `pom.xml`.

The scenarios call the live PokeAPI, so network outages or API availability can affect test results.
