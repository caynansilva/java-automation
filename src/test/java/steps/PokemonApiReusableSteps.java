package steps;

import com.caynan.qa.api.PokemonAPI;
import com.caynan.qa.types.PokemonResponse;
import com.caynan.qa.utils.Helper;
import io.restassured.response.Response;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PokemonApiReusableSteps {

    private final PokemonAPI pokemonAPI = new PokemonAPI();
    private Response response;
    private PokemonResponse pokemonResponse;

    public void requestPokemonByName(String name) {
        response = pokemonAPI.getPokemon(name);
        pokemonResponse = null;
    }

    public void requestPokemonById(int id) {
        requestPokemonByName(Integer.toString(id));
    }

    public void assertStatusCode(int expectedStatusCode) {
        Helper.assertStatusCode(response, expectedStatusCode);
    }

    public void assertPokemonName(String expectedName) {
        assertEquals(expectedName, getPokemonResponse().name());
    }

    public void assertPokemonNameContains(String expectedPart) {
        assertTrue(
                pokemonAPI.assertPokemonNameContains(response, expectedPart),
                "Expected the Pokémon name to contain " + expectedPart
        );
    }

    public void assertPokemonId(int expectedId) {
        assertEquals(expectedId, getPokemonResponse().id());
    }

    public void assertPokemonContractIsValid() {
        PokemonResponse pokemon = getPokemonResponse();

        assertNotNull(pokemon.id(), "Pokémon id must be present");
        assertTrue(pokemon.id() > 0, "Pokémon id must be positive");
        assertNotNull(pokemon.name(), "Pokémon name must be present");
        assertFalse(pokemon.name().isBlank(), "Pokémon name must not be blank");
        assertNotNull(pokemon.height(), "Pokémon height must be present");
        assertTrue(pokemon.height() > 0, "Pokémon height must be positive");
        assertNotNull(pokemon.weight(), "Pokémon weight must be present");
        assertTrue(pokemon.weight() > 0, "Pokémon weight must be positive");

        assertRequiredCollection(pokemon.abilities(), "abilities");
        assertRequiredCollection(pokemon.types(), "types");
        assertRequiredCollection(pokemon.stats(), "stats");
    }

    public void assertPokemonHasType(String expectedType) {
        List<PokemonResponse.PokemonType> types = getPokemonResponse().types();
        assertNotNull(types, "Pokémon types must be present");

        assertTrue(
                types.stream().anyMatch(type -> type != null
                        && type.type() != null
                        && expectedType.equalsIgnoreCase(type.type().name())),
                "Expected Pokémon to have type " + expectedType
        );
    }

    public void assertPokemonHasAbility(String expectedAbility) {
        List<PokemonResponse.PokemonAbility> abilities =
                getPokemonResponse().abilities();
        assertNotNull(abilities, "Pokémon abilities must be present");

        assertTrue(
                abilities.stream().anyMatch(ability -> ability != null
                        && ability.ability() != null
                        && expectedAbility.equalsIgnoreCase(
                                ability.ability().name()
                        )),
                "Expected Pokémon to have ability " + expectedAbility
        );
    }

    public void assertJsonContentType() {
        Helper.assertHeaderExists(response, "Content-Type");
        String contentType = Helper.getContentType(response);

        assertNotNull(contentType, "Response Content-Type must be present");
        assertTrue(
                contentType.toLowerCase(Locale.ROOT).contains("application/json"),
                "Expected JSON content type but received " + contentType
        );
    }

    private PokemonResponse getPokemonResponse() {
        if (pokemonResponse == null) {
            Helper.assertStatusCode(response, 200);
            pokemonResponse = Helper.parseResponse(
                    response,
                    PokemonResponse.class
            );
        }

        return pokemonResponse;
    }

    private static void assertRequiredCollection(
            List<?> collection,
            String collectionName
    ) {
        assertNotNull(
                collection,
                "Pokémon " + collectionName + " must be present"
        );
        assertFalse(
                collection.isEmpty(),
                "Pokémon " + collectionName + " must not be empty"
        );
    }
}
