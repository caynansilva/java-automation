package test_steps;

public class PK_003_API_TEST_STEPS {

    private final PokemonApiReusableSteps reusableSteps =
            new PokemonApiReusableSteps();

    public void GIVEN_THAT_WE_REQUEST_AN_INVALID_POKEMON_NAME() {
        reusableSteps.requestPokemonByName(
                "pokemon-that-does-not-exist-automation-xyz"
        );
    }

    public void THEN_ASSERT_RESPONSE_CODE_IS_404() {
        reusableSteps.assertStatusCode(404);
    }
}
