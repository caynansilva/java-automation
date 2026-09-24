package test_steps;

public class PK_007_API_TEST_STEPS {

    private final PokemonApiReusableSteps reusableSteps =
            new PokemonApiReusableSteps();

    public void GIVEN_THAT_WE_REQUEST_PIKACHU() {
        reusableSteps.requestPokemonByName("pikachu");
    }

    public void THEN_ASSERT_RESPONSE_CODE_IS_200() {
        reusableSteps.assertStatusCode(200);
    }

    public void AND_ASSERT_RESPONSE_HAS_JSON_CONTENT_TYPE() {
        reusableSteps.assertJsonContentType();
    }
}
