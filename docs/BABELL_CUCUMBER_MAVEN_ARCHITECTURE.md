# BABELL Cucumber Test Architecture

## Purpose

BABELL uses a test architecture designed around:

- Single Responsibility
- human readability
- test isolation
- predictable file ownership
- maintainability at scale
- reusable implementation
- minimal unnecessary abstraction

The objective is not to minimize the number of files at all costs.

The objective is also not to create files simply because a framework technically allows another abstraction layer.

BABELL prefers:

> **One meaningful responsibility per artifact.**

For Java + Maven + Cucumber, the recommended scenario structure is:

> **One Test, One Feature, One Test Definition. Shared behavior belongs to reusable framework Steps.**

---

# Core Architecture

Each independently managed test scenario should normally have:

```text
PK_001_API_TEST.feature
PK_001_API_TEST.java
```

The Feature describes the behavior.

The Java Test Definition connects that behavior to BABELL and provides the executable Cucumber configuration required by the Java/Maven test environment.

Shared implementation remains outside both.

The complete flow becomes:

```text
PK_001_API_TEST.feature
        ↓
PK_001_API_TEST.java
        ↓
PokemonApiReusableSteps.java
        ↓
PokemonAPI.java
        ↓
BABELL Helper
        ↓
RestAssured
```

---

# Why this architecture changed

An earlier architecture separated every scenario into three artifacts:

```text
PK_001_API_TEST.feature
PK_001_API_TEST.java
PK_001_API_TEST_STEPS.java
```

Conceptually:

```text
Feature
   ↓
Runner
   ↓
Cucumber Steps
   ↓
Reusable Steps
```

Although technically valid, this creates a problem in this specific Java + Maven + Cucumber architecture.

The runner class frequently contains nothing except configuration:

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/PK_001_API_TEST.feature")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "tests.pk001"
)
public class PK_001_API_TEST {
}
```

The class does not:

- define test behavior
- contain assertions
- perform test actions
- contain reusable logic
- describe the scenario

Its only responsibility is connecting Maven/JUnit Platform to Cucumber.

At the same time, another Java class contains only the Cucumber bindings.

This creates two Java files for what is conceptually one executable test definition.

BABELL does not consider this separation valuable enough to justify the additional indirection.

---

# Architectural decision

For this scenario, BABELL combines:

```text
Cucumber runner configuration
+
scenario-specific Cucumber bindings
```

inside the same Java Test Definition.

Therefore:

```text
PK_001_API_TEST.java
```

has a clear responsibility:

> **Represent the executable Java definition of PK_001 and connect its Gherkin scenario to reusable BABELL behavior.**

The file is no longer an empty technical bridge.

It becomes the Java representation of the test.

---

# Recommended project structure

```text
src/
├── main/
│   └── java/
│       └── com/caynan/qa/
│           ├── api/
│           │   └── PokemonAPI.java
│           │
│           └── utils/
│               └── Helper.java
│
└── test/
    ├── java/
    │   ├── tests/
    │   │   ├── pk001/
    │   │   │   └── PK_001_API_TEST.java
    │   │   │
    │   │   ├── pk002/
    │   │   │   └── PK_002_API_TEST.java
    │   │   │
    │   │   ├── pk003/
    │   │   │   └── PK_003_API_TEST.java
    │   │   │
    │   │   └── ...
    │   │
    │   └── steps/
    │       └── PokemonApiReusableSteps.java
    │
    └── resources/
        └── features/
            ├── PK_001_API_TEST.feature
            ├── PK_002_API_TEST.feature
            ├── PK_003_API_TEST.feature
            └── ...
```

---

# Feature responsibility

Example:

```gherkin
@PK_001
Feature: PK_001 - Get Pokemon by name

  Scenario: Get Pokemon by name
    Given I request Pokemon "pikachu"
    Then the Pokemon name should contain "pikachu"
    And the response status code should be 200
```

The Feature answers:

> **What behavior are we testing?**

It should contain:

- business-readable behavior
- test scenario
- expected outcome
- scenario parameters

It should not contain knowledge about:

- RestAssured
- Java implementation
- HTTP client internals
- BABELL Helper internals
- response deserialization
- framework infrastructure

The Feature represents intent.

---

# Java Test Definition responsibility

Example:

```java
package tests.pk001;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import steps.PokemonApiReusableSteps;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/PK_001_API_TEST.feature")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "tests.pk001"
)
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty"
)
public class PK_001_API_TEST {

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

The Java Test Definition answers:

> **How does this Feature execute through BABELL?**

Its responsibilities are:

- identify the associated Feature
- configure Cucumber execution
- define scenario-specific Gherkin bindings
- delegate behavior to reusable BABELL Steps

It should remain small.

It should not contain the actual low-level automation implementation.

---

# Why Runner and Step Binding are combined

BABELL follows Single Responsibility, but Single Responsibility does not mean:

> one technical concept per file regardless of context.

It means:

> one reason for the artifact to change.

For:

```text
PK_001_API_TEST.java
```

the reason to change is:

> **PK_001's executable Java definition changed.**

Its runner configuration and its bindings belong to the same test.

They share the same ownership.

They share the same lifecycle.

They are reviewed together.

They are executed together.

Separating them creates indirection without creating meaningful architectural independence.

---

# Avoid unnecessary fragmentation

This architecture intentionally avoids:

```text
PK_001_API_TEST.java
PK_001_API_TEST_STEPS.java
```

when the first file contains only:

```java
@Suite
@IncludeEngines(...)
@SelectClasspathResource(...)
```

and the second contains only:

```java
@Given(...)
@Then(...)
```

For BABELL, this is unnecessary fragmentation.

The two files are not independently useful.

They represent the same test boundary.

Therefore they should be kept together.

---

# Single Responsibility does not mean maximum separation

BABELL does not interpret SRP as:

```text
annotation → new file

configuration → new file

binding → new file

assertion → new file

request → new file
```

This would increase structural complexity instead of reducing it.

BABELL uses responsibility boundaries that humans can understand.

For example:

```text
Feature
    responsibility:
    test behavior

Test Definition
    responsibility:
    executable mapping of that behavior

Reusable Steps
    responsibility:
    reusable test actions/assertions

API/Page Object
    responsibility:
    domain interaction

Helper
    responsibility:
    framework-level implementation
```

This creates meaningful boundaries.

---

# Reusable Steps remain separate

Although the runner and Cucumber bindings are combined, reusable behavior must remain separate.

Example:

```java
public class PokemonApiReusableSteps {

    public void requestPokemonByName(String pokemon) {
        // reusable behavior
    }

    public void assertStatusCode(int expectedStatusCode) {
        // reusable assertion
    }

    public void assertPokemonNameContains(String expectedName) {
        // reusable assertion
    }
}
```

Multiple tests may use this same implementation:

```text
PK_001_API_TEST ──────┐
                      │
PK_002_API_TEST ──────┼──→ PokemonApiReusableSteps
                      │
PK_003_API_TEST ──────┘
```

This preserves reuse without centralizing Cucumber bindings.

---

# Important duplication rule

BABELL distinguishes between:

> **structural duplication**

and:

> **implementation duplication**

Structural repetition is acceptable when it improves ownership and discoverability.

For example:

```text
PK_001_API_TEST.java
PK_002_API_TEST.java
PK_003_API_TEST.java
```

may all contain similar Cucumber annotations.

That is acceptable.

What should not be copied is real implementation such as:

```java
given()
    .when()
    .get(...)
    .then();
```

or repeated response parsing and assertion logic.

That belongs in reusable layers.

The rule is:

> **Duplicating small structural declarations is acceptable. Duplicating behavior is not.**

---

# Cucumber Glue isolation

Each Test Definition can use its own package as Cucumber glue.

Example:

```text
tests.pk001
tests.pk002
tests.pk003
```

Then:

```java
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "tests.pk001"
)
```

allows PK_001 to load only its own bindings.

This is important because independent tests may intentionally use the same Gherkin expression:

```java
@Given("I request Pokemon {string}")
```

Without isolated glue packages, Cucumber could discover multiple identical bindings and report ambiguous step definitions.

Package isolation allows each test to own its vocabulary independently.

---

# Human-first navigation

A test should be easy to locate without understanding the entire repository.

If CI reports:

```text
PK_057 failed
```

a developer should immediately know to inspect:

```text
PK_057_API_TEST.feature
PK_057_API_TEST.java
```

There should be no need to search through:

```text
AllPokemonFeatures.feature
PokemonStepDefinitions.java
GlobalCucumberRunner.java
```

to understand one test.

The project structure itself should act as documentation.

---

# Independent execution

A scenario should remain independently executable.

For example:

```bash
mvn test -Dtest=PK_057_API_TEST
```

should execute the Java Test Definition associated with:

```text
PK_057_API_TEST.feature
```

This provides:

- easier debugging
- faster local execution
- simpler CI diagnostics
- clear test ownership
- predictable test selection

---

# Scaling

A project with 200 test scenarios may contain:

```text
PK_001_API_TEST.feature
PK_001_API_TEST.java

PK_002_API_TEST.feature
PK_002_API_TEST.java

...

PK_200_API_TEST.feature
PK_200_API_TEST.java
```

BABELL considers this preferable to:

```text
all_api_tests.feature
AllApiStepDefinitions.java
RunAllApiTests.java
```

The number of files grows linearly.

The complexity of each file remains small.

This is an intentional architectural trade-off.

---

# Responsibilities by layer

| Layer | Responsibility |
|---|---|
| `.feature` | Describe test behavior |
| `PK_xxx_API_TEST.java` | Execute Feature and bind Gherkin to BABELL |
| `ReusableSteps` | Shared test actions and assertions |
| `API / Page Object` | Domain interaction |
| `Helper` | BABELL framework functionality |
| RestAssured / Playwright / Selenium | Low-level automation engine |

A higher-level layer should not implement responsibilities belonging to a lower layer.

---

# API example

```text
PK_001_API_TEST.feature
        ↓
PK_001_API_TEST.java
        ↓
PokemonApiReusableSteps
        ↓
PokemonAPI
        ↓
Helper
        ↓
RestAssured
```

---

# Future UI example

The same architecture can be used for UI automation:

```text
LOGIN_001_UI_TEST.feature
        ↓
LOGIN_001_UI_TEST.java
        ↓
LoginReusableSteps
        ↓
LoginPage
        ↓
WebElement
        ↓
PlaywrightAdapter
```

This allows BABELL to preserve the same conceptual architecture across different automation engines.

---

# Architectural anti-patterns

## Global Feature file

Avoid:

```text
all_api_tests.feature
```

containing dozens or hundreds of unrelated scenarios.

---

## Global Step Definition class

Avoid:

```text
ApiStepDefinitions.java
```

containing bindings for the entire automation suite.

This eventually becomes a God Object for Cucumber.

---

## Empty runner per scenario plus separate binding

Avoid unnecessary patterns such as:

```text
PK_001_API_TEST.java
PK_001_API_TEST_STEPS.java
```

when the runner contains no meaningful behavior beyond selecting that same scenario.

In this situation the split increases navigation cost without increasing modularity.

---

## Low-level automation inside Test Definition

Avoid:

```java
@Given("I request Pokemon {string}")
public void requestPokemon(String pokemon) {

    given()
        .baseUri(...)
        .get(...)
        .then();
}
```

The Test Definition should delegate:

```java
@Given("I request Pokemon {string}")
public void requestPokemon(String pokemon) {
    steps.requestPokemonByName(pokemon);
}
```

---

# Decision guideline

Before creating another abstraction or file, ask:

> **Does this component have an independent responsibility and lifecycle?**

If yes:

```text
separate it
```

If no:

```text
keep it with the component that owns the responsibility
```

For the current Java + Maven + Cucumber architecture:

```text
Runner configuration
+
Cucumber bindings
```

belong to the same test ownership boundary.

Therefore they remain together.

Reusable behavior, however, has an independent responsibility and remains separated.

---

# Core BABELL principles

## 1. One Test, One Feature, One Test Definition

```text
TEST
├── Feature
└── Java Test Definition
```

---

## 2. Shared behavior belongs below the Test Definition

```text
Test Definition
        ↓
Reusable Steps
```

---

## 3. Structural repetition is acceptable

Small repeated declarations are preferable to giant centralized files.

---

## 4. Implementation duplication is not acceptable

Reusable behavior belongs in shared abstractions.

---

## 5. Files should have an obvious reason to exist

Every file should be explainable in one sentence.

Example:

```text
PK_001_API_TEST.feature
→ describes PK_001

PK_001_API_TEST.java
→ executes and binds PK_001

PokemonApiReusableSteps.java
→ contains reusable Pokemon test actions

PokemonAPI.java
→ represents Pokemon API operations

Helper.java
→ provides BABELL HTTP capabilities
```

If the reason for a file cannot be explained clearly, its abstraction should be questioned.

---

# Final architecture principle

BABELL does not optimize for:

> **the maximum possible separation of code.**

BABELL optimizes for:

> **the clearest meaningful separation of responsibilities.**

The preferred architecture is therefore:

```text
Feature
   ↓
Test Definition
   ↓
Reusable Steps
   ↓
Domain Object
   ↓
BABELL Core
   ↓
Automation Engine
```

Or, in one sentence:

> **Keep the test isolated, keep the implementation reusable, and do not create abstraction layers that cannot justify their own existence.**
