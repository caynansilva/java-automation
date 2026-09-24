package test_steps;

public class PK_005_API_TEST_STEPS {

    private final PokemonApiReusableSteps reusableSteps =
            new PokemonApiReusableSteps();

    public void GIVEN_THAT_WE_REQUEST_BULBASAUR() {
        reusableSteps.requestPokemonByName("bulbasaur");
    }

    public void THEN_ASSERT_POKEMON_HAS_TYPE_GRASS() {
        reusableSteps.assertPokemonHasType("grass");
    }

    public void AND_ASSERT_POKEMON_HAS_TYPE_POISON() {
        reusableSteps.assertPokemonHasType("poison");
    }
}
