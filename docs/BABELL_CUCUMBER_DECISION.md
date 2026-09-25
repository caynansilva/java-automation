# BABELL Cucumber Test Architecture

> **Superseded architecture record:** This document records the earlier three-artifact design with a separate runner and per-test binding class. The current rule is **One Test, One Feature, One Test Definition**: suite metadata and that PK's Cucumber bindings live together in `tests.pkNNN/PK_NNN_API_TEST.java`. See [BABELL_CUCUMBER_MAVEN_ARCHITECTURE.md](BABELL_CUCUMBER_MAVEN_ARCHITECTURE.md) for the current source of truth. This document is retained for historical context.

## Objective

The BABELL test architecture is designed around a simple principle:

> **One Test, One Feature, One Runner, One Step Binding. Shared behavior belongs in reusable framework-level Steps.**

The goal is not to minimize the number of files.

The goal is to maximize:

- maintainability
- readability
- traceability
- test isolation
- human reviewability
- reuse of implementation
- clear responsibility boundaries

A large number of small and predictable files is preferred over a small number of extremely large files containing unrelated test scenarios.

---

# Why this architecture exists

A common Cucumber implementation starts with something simple:

```text
pokemon_api.feature
PokemonStepDefinitions.java
RunCucumberTest.java
```

For a small project, this works well.

However, as the automation suite grows, this structure can become difficult to maintain.

A single feature file may eventually contain:

```text
50
100
200+
```

scenarios.

The same happens with the Step Definition class.

A file such as:

```text
PokemonStepDefinitions.java
```

can gradually become responsible for hundreds of unrelated bindings.

At that point, the file no longer represents one responsibility.

It becomes a central registry for an entire test suite.

This creates several problems.

---

# Problems with monolithic Step Definition files

## Difficult navigation

When a test fails, a developer or QA should be able to immediately locate the files responsible for that test.

With a monolithic structure:

```text
pokemon_api.feature
PokemonStepDefinitions.java
```

finding `PK_137` means searching inside potentially thousands of lines.

With BABELL:

```text
PK_137_API_TEST.feature
PK_137_API_TEST.java
PK_137_API_TEST_STEPS.java
```

the ownership of the test is immediately visible.

---

## Difficult code reviews

Suppose a Pull Request changes only `PK_042`.

With a monolithic architecture, the change may appear inside:

```text
PokemonStepDefinitions.java
```

which may also contain the implementation and bindings of another 150 tests.

The reviewer needs to understand a much larger context than necessary.

With BABELL, the change is naturally isolated:

```text
PK_042_API_TEST.feature
PK_042_API_TEST_STEPS.java
```

This makes reviews smaller and easier to reason about.

---

## Single Responsibility Principle

A Step Definition file should not become responsible for an entire application or API.

BABELL applies the Single Responsibility Principle to the test structure itself.

Each test owns its Cucumber representation.

For example:

```text
PK_001_API_TEST.feature
PK_001_API_TEST.java
PK_001_API_TEST_STEPS.java
```

These files exist specifically to represent `PK_001`.

They do not need to know how `PK_002`, `PK_003` or `PK_100` work.

---

# BABELL architecture

The recommended structure is:

```text
src/
├── main/
│   └── java/
│       └── com/caynan/qa/
│           ├── api/
│           │   └── PokemonAPI.java
│           └── utils/
│               └── Helper.java
│
└── test/
    ├── java/
    │   ├── tests/
    │   │   ├── PK_001_API_TEST.java
    │   │   ├── PK_002_API_TEST.java
    │   │   └── PK_003_API_TEST.java
    │   │
    │   └── test_steps/
    │       ├── reusable/
    │       │   └── PokemonApiReusableSteps.java
    │       │
    │       └── cucumber/
    │           ├── pk001/
    │           │   └── PK_001_API_TEST_STEPS.java
    │           ├── pk002/
    │           │   └── PK_002_API_TEST_STEPS.java
    │           └── pk003/
    │               └── PK_003_API_TEST_STEPS.java
    │
    └── resources/
        └── features/
            ├── PK_001_API_TEST.feature
            ├── PK_002_API_TEST.feature
            └── PK_003_API_TEST.feature
```

---

# Test execution flow

Each test follows the same predictable flow:

```text
PK_001_API_TEST.feature
        ↓
PK_001_API_TEST.java
        ↓
PK_001_API_TEST_STEPS.java
        ↓
PokemonApiReusableSteps.java
        ↓
PokemonAPI.java
        ↓
BABELL Helper
        ↓
RestAssured
```

Each layer has a different responsibility.

---

# Feature file

Example:

```gherkin
@PK_001
Feature: PK_001 - Get Pokemon by name

  Scenario: Get Pokemon by name
    Given I request Pokemon "pikachu"
    Then the Pokemon name should contain "pikachu"
    And the response status code should be 200
```

The feature describes:

> **What is being tested?**

It should not know:

- RestAssured
- HTTP implementation details
- deserialization details
- framework internals

The Feature represents behavior.

---

# Test Runner

Example:

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/PK_001_API_TEST.feature")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "test_steps.cucumber.pk001"
)
public class PK_001_API_TEST {
}
```

The runner answers:

> **Which test should be executed?**

Its responsibility is intentionally very small.

It connects:

```text
Feature
+
Cucumber Glue
```

Nothing more.

This also makes individual execution possible:

```bash
mvn test -Dtest=PK_001_API_TEST
```

---

# Cucumber Step Binding

Example:

```java
public class PK_001_API_TEST_STEPS {

    private final PokemonApiReusableSteps steps =
            new PokemonApiReusableSteps();

    @Given("I request Pokemon {string}")
    public void requestPokemon(String pokemon) {
        steps.requestPokemonByName(pokemon);
    }

    @Then("the Pokemon name should contain {string}")
    public void assertPokemonName(String name) {
        steps.assertPokemonNameContains(name);
    }

    @Then("the response status code should be {int}")
    public void assertStatusCode(int statusCode) {
        steps.assertStatusCode(statusCode);
    }
}
```

This layer answers:

> **How does this particular Gherkin sentence connect to BABELL?**

The binding should remain small.

It should contain primarily:

```text
Cucumber annotation
+
parameters
+
delegation
```

It should not become the real implementation of the test framework.

---

# Reusable Steps

Reusable behavior belongs in a lower layer.

For example:

```java
public class PokemonApiReusableSteps {

    public void requestPokemonByName(String pokemon) {
        // reusable implementation
    }

    public void assertStatusCode(int expectedStatusCode) {
        // reusable assertion
    }

    public void assertPokemonNameContains(String expectedName) {
        // reusable assertion
    }
}
```

This allows multiple tests to reuse the same implementation:

```text
PK_001_API_TEST_STEPS ─────┐
                           │
PK_002_API_TEST_STEPS ─────┼──→ PokemonApiReusableSteps
                           │
PK_003_API_TEST_STEPS ─────┘
```

The Step Binding belongs to the test.

The reusable implementation belongs to the framework/test domain layer.

---

# Duplication rule

BABELL intentionally makes an important distinction:

> **Duplicating files is acceptable. Duplicating implementation is not.**

Having:

```text
PK_001_API_TEST_STEPS.java
PK_002_API_TEST_STEPS.java
PK_003_API_TEST_STEPS.java
```

is not considered a problem.

These files provide:

- ownership
- discoverability
- test isolation
- readable mapping between test and implementation

However, copying the same HTTP logic into all three files would be a problem.

For example, this should not be repeated:

```java
given()
    .when()
    .get(...)
    .then();
```

Instead, all test bindings should delegate to reusable BABELL functionality.

---

# Why not one global Step Definition file?

A global file initially looks simpler:

```text
PokemonStepDefinitions.java
```

But over time it may become:

```text
PokemonStepDefinitions.java

@Given(...)
@When(...)
@Then(...)
@Given(...)
@Then(...)
@Given(...)
@When(...)
@Then(...)
...
```

Eventually the file may contain hundreds of methods belonging to completely different test cases.

This introduces what can effectively become a:

> **God Step Definition**

The class knows too much and has too many reasons to change.

BABELL avoids this by keeping scenario bindings close to the scenario itself.

---

# Human-first maintainability

BABELL automation should be understandable without requiring an AI agent to navigate the project.

A developer should be able to see:

```text
PK_057
```

and immediately search for:

```text
PK_057_API_TEST.feature
PK_057_API_TEST.java
PK_057_API_TEST_STEPS.java
```

The structure itself becomes documentation.

No additional lookup table should be required to understand where a test is implemented.

---

# Test isolation

Another advantage is isolated execution.

For example:

```bash
mvn test -Dtest=PK_057_API_TEST
```

A QA investigating a failure does not need to execute an entire feature containing dozens of scenarios.

The test can be:

```text
identified
executed
debugged
modified
reviewed
```

independently.

---

# Cucumber Glue isolation

Cucumber step expressions can become ambiguous if multiple Step Definition classes using identical expressions are loaded into the same Glue scope.

For example:

```java
@Given("I request Pokemon {string}")
```

may legitimately exist in multiple isolated tests.

Therefore BABELL can isolate Cucumber bindings by package:

```text
test_steps.cucumber.pk001
test_steps.cucumber.pk002
test_steps.cucumber.pk003
```

Each test runner selects only its corresponding Glue package.

Example:

```java
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "test_steps.cucumber.pk001"
)
```

This allows individual tests to remain independent without creating global Cucumber step conflicts.

---

# Separation of responsibilities

The responsibilities should remain:

| Layer | Responsibility |
|---|---|
| `.feature` | Describe test behavior |
| `PK_xxx_API_TEST` | Select and execute the scenario |
| `PK_xxx_API_TEST_STEPS` | Bind Gherkin to Java |
| `ReusableSteps` | Shared testing actions and assertions |
| `PokemonAPI` | API/domain-specific operations |
| `Helper` | BABELL HTTP/framework operations |
| RestAssured | Low-level HTTP implementation |

A higher layer should delegate technical behavior downward.

For example:

```text
Feature
does not know RestAssured

Step Binding
does not implement HTTP

Reusable Steps
does not need to know Cucumber

PokemonAPI
does not need to know test scenario IDs

Helper
does not need to know Pokemon
```

This separation is one of the core architectural goals of BABELL.

---

# Scaling example

With 200 tests, BABELL accepts having:

```text
PK_001_API_TEST.feature
PK_001_API_TEST.java
PK_001_API_TEST_STEPS.java

PK_002_API_TEST.feature
PK_002_API_TEST.java
PK_002_API_TEST_STEPS.java

...

PK_200_API_TEST.feature
PK_200_API_TEST.java
PK_200_API_TEST_STEPS.java
```

This is intentionally preferred over:

```text
all_api_tests.feature
ApiStepDefinitions.java
RunAllApiTests.java
```

where unrelated tests continuously accumulate in the same files.

The number of files increases.

The complexity of each individual file does not.

This is the desired trade-off.

---

# Core principle

The BABELL test architecture can be summarized as:

> **One Test, One Feature, One Runner, One Step Binding.**

And:

> **Shared behavior should be reusable, but test ownership should remain isolated.**

BABELL prefers explicit relationships between small files over implicit relationships hidden inside large centralized files.

The purpose is not to optimize the project for the smallest possible file count.

The purpose is to keep the automation suite maintainable by humans as it grows.
