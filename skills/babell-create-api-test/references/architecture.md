# BABELL API Test Architecture

## Canonical structure

Each independently managed API scenario has one feature and one Java Test Definition:

```text
src/test/resources/features/PK_008_API_TEST.feature
src/test/java/tests/pk008/PK_008_API_TEST.java
```

The Java package and Cucumber glue are both `tests.pk008`. The Test Definition contains the JUnit Platform Suite metadata and the scenario's Cucumber bindings. It delegates to `steps.PokemonApiReusableSteps`.

```text
Feature → Test Definition and Cucumber bindings → PokemonApiReusableSteps
        → PokemonAPI → Helper → Rest Assured
```

The authoritative project guide is `docs/BABELL_CUCUMBER_MAVEN_ARCHITECTURE.md`. Existing PK_001–PK_007 files are concrete examples; inspect their actual content before creating a new test.

## Test Definition pattern

```java
package tests.pk008;

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
@SelectClasspathResource("features/PK_008_API_TEST.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "tests.pk008")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class PK_008_API_TEST {

    private final PokemonApiReusableSteps steps = new PokemonApiReusableSteps();

    @Given("I request Pokemon {string}")
    public void requestPokemon(String pokemon) {
        steps.requestPokemonByName(pokemon);
    }

    @Then("the response status code should be {int}")
    public void assertStatusCode(int status) {
        steps.assertStatusCode(status);
    }
}
```

Use only bindings required by the feature. The Test Definition translates Gherkin phrases and delegates; it does not own response parsing, reusable assertions, or request construction.

## Identifier and feature rules

- Find the highest assigned PK identifier and the unused identifiers before selecting the next available `PK_NNN`; never overwrite an existing feature or class.
- Match the feature file name, Java class name, selected classpath resource, package suffix, glue value, and `@PK_NNN` tag.
- Keep one scenario in the feature and preserve the user's requested observable behavior.
- Do not add duplicate JUnit `@Test` scenarios, separate per-test `*_STEPS.java` files, or global feature/glue/runner classes.
- Search reusable Steps, API operations, response types, and Helpers before adding behavior. If a reusable capability is missing, follow the sibling `babell-create-reusable-step` skill.

## Validation

Run the isolated test first:

```shell
mvn test -Dtest=PK_008_API_TEST
```

Then run the full suite:

```shell
mvn clean test
```

Surefire includes classes matching `PK_*_API_TEST.java`. If the direct selector discovers zero tests, inspect the suite class/package and Surefire configuration instead of adding another runner.
