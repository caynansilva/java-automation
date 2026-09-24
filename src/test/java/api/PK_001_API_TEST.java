package api;

import org.junit.jupiter.api.Test;
import test_steps.PK_001_API_TEST_STEPS;

class PK_001_API_TEST {

    private final PK_001_API_TEST_STEPS steps = new PK_001_API_TEST_STEPS();

    @Test
    void getsPikachu() {
        steps.GIVEN_THAT_WE_SEND_API_REQUEST_A_POKEMON_NAME();
        steps.THEN_ASSERT_RESPONSE_INCLUDES_POKEMON_NAME();
        steps.AND_ASSERT_THAT_RESPONSE_CODE_IS_200();
    }
}
