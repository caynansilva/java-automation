package api;

import org.junit.jupiter.api.Test;
import test_steps.PK_005_API_TEST_STEPS;

class PK_005_API_TEST {

    private final PK_005_API_TEST_STEPS steps = new PK_005_API_TEST_STEPS();

    @Test
    void validatesBulbasaurTypes() {
        steps.GIVEN_THAT_WE_REQUEST_BULBASAUR();
        steps.THEN_ASSERT_POKEMON_HAS_TYPE_GRASS();
        steps.AND_ASSERT_POKEMON_HAS_TYPE_POISON();
    }
}
