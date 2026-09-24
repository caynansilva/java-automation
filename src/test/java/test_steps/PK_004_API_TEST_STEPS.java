package test_steps;

public class PK_004_API_TEST_STEPS {

    private final PokemonApiReusableSteps reusableSteps =
            new PokemonApiReusableSteps();

    public void GIVEN_THAT_WE_REQUEST_PIKACHU() {
        reusableSteps.requestPokemonByName("pikachu");
    }

    public void THEN_ASSERT_POKEMON_RESPONSE_CONTRACT_IS_VALID() {
        reusableSteps.assertPokemonContractIsValid();
    }
}
