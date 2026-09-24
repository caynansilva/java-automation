package com.caynan.qa.api;

import com.caynan.qa.types.PokemonResponse;
import com.caynan.qa.types.RequestType;
import com.caynan.qa.utils.Helper;
import io.restassured.response.Response;

public class PokemonAPI {

    private static final String BASE_URL =
            "https://pokeapi.co/api/v2/pokemon";

    public Response getPokemon(String name) {

        return Helper.sendAPIRequest(
                RequestType.GET,
                BASE_URL + "/" + name,
                null,
                null
        );
    }

    public Boolean assertPokemonNameContains(Response response, String name) {
        if (response == null || name == null || name.isBlank()) {
            return false;
        }

        if (response.statusCode() != 200) {
            return false;
        }

        PokemonResponse pokemon = Helper.parseResponse(
                response,
                PokemonResponse.class
        );

        return pokemon.name() != null
                && pokemon.name().contains(name);
    }
}
