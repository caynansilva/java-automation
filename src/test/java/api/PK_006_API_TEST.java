package api;

import org.junit.jupiter.api.Test;
import test_steps.PK_006_API_TEST_STEPS;

class PK_006_API_TEST {

    private final PK_006_API_TEST_STEPS steps = new PK_006_API_TEST_STEPS();

    @Test
    void validatesPikachuAbility() {
        steps.GIVEN_THAT_WE_REQUEST_PIKACHU();
        steps.THEN_ASSERT_POKEMON_HAS_ABILITY_STATIC();
    }
}
