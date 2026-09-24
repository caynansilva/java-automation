package api;

import org.junit.jupiter.api.Test;
import test_steps.PK_002_API_TEST_STEPS;

class PK_002_API_TEST {

    private final PK_002_API_TEST_STEPS steps = new PK_002_API_TEST_STEPS();

    @Test
    void getsPokemonById() {
        steps.GIVEN_THAT_WE_REQUEST_POKEMON_BY_ID_25();
        steps.THEN_ASSERT_POKEMON_NAME_IS_PIKACHU();
        steps.AND_ASSERT_POKEMON_ID_IS_25();
        steps.AND_ASSERT_RESPONSE_CODE_IS_200();
    }
}
