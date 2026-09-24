package api;

import org.junit.jupiter.api.Test;
import test_steps.PK_003_API_TEST_STEPS;

class PK_003_API_TEST {

    private final PK_003_API_TEST_STEPS steps = new PK_003_API_TEST_STEPS();

    @Test
    void returnsNotFoundForUnknownPokemon() {
        steps.GIVEN_THAT_WE_REQUEST_AN_INVALID_POKEMON_NAME();
        steps.THEN_ASSERT_RESPONSE_CODE_IS_404();
    }
}
