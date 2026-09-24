package test_steps;

public class PK_006_API_TEST_STEPS {

    private final PokemonApiReusableSteps reusableSteps =
            new PokemonApiReusableSteps();

    public void GIVEN_THAT_WE_REQUEST_PIKACHU() {
        reusableSteps.requestPokemonByName("pikachu");
    }

    public void THEN_ASSERT_POKEMON_HAS_ABILITY_STATIC() {
        reusableSteps.assertPokemonHasAbility("static");
    }
}
