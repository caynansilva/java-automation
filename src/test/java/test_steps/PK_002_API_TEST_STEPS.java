package test_steps;

public class PK_002_API_TEST_STEPS {

    private final PokemonApiReusableSteps reusableSteps =
            new PokemonApiReusableSteps();

    public void GIVEN_THAT_WE_REQUEST_POKEMON_BY_ID_25() {
        reusableSteps.requestPokemonById(25);
    }

    public void THEN_ASSERT_POKEMON_NAME_IS_PIKACHU() {
        reusableSteps.assertPokemonName("pikachu");
    }

    public void AND_ASSERT_POKEMON_ID_IS_25() {
        reusableSteps.assertPokemonId(25);
    }

    public void AND_ASSERT_RESPONSE_CODE_IS_200() {
        reusableSteps.assertStatusCode(200);
    }
}
