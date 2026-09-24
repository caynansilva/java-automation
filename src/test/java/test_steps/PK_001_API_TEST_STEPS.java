package test_steps;

public class PK_001_API_TEST_STEPS {

    private final PokemonApiReusableSteps reusableSteps =
            new PokemonApiReusableSteps();

    public void GIVEN_THAT_WE_SEND_API_REQUEST_A_POKEMON_NAME() {
        reusableSteps.requestPokemonByName("pikachu");
    }

    public void THEN_ASSERT_RESPONSE_INCLUDES_POKEMON_NAME() {
        reusableSteps.assertPokemonNameContains("pikachu");
    }

    public void AND_ASSERT_THAT_RESPONSE_CODE_IS_200() {
        reusableSteps.assertStatusCode(200);
    }
}
