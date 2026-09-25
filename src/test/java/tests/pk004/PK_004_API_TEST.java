package tests.pk004;

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
@SelectClasspathResource("features/PK_004_API_TEST.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "tests.pk004")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class PK_004_API_TEST {

    private final PokemonApiReusableSteps steps = new PokemonApiReusableSteps();

    @Given("I request Pokemon {string}")
    public void requestPokemon(String pokemon) {
        steps.requestPokemonByName(pokemon);
    }

    @Then("the Pokemon response contract should be valid")
    public void assertPokemonContract() {
        steps.assertPokemonContractIsValid();
    }
}
