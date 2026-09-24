package api;

import org.junit.jupiter.api.Test;
import test_steps.PK_004_API_TEST_STEPS;

class PK_004_API_TEST {

    private final PK_004_API_TEST_STEPS steps = new PK_004_API_TEST_STEPS();

    @Test
    void validatesPokemonResponseContract() {
        steps.GIVEN_THAT_WE_REQUEST_PIKACHU();
        steps.THEN_ASSERT_POKEMON_RESPONSE_CONTRACT_IS_VALID();
    }
}
