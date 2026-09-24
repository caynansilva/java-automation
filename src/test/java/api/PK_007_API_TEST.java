package api;

import org.junit.jupiter.api.Test;
import test_steps.PK_007_API_TEST_STEPS;

class PK_007_API_TEST {

    private final PK_007_API_TEST_STEPS steps = new PK_007_API_TEST_STEPS();

    @Test
    void validatesResponseMetadata() {
        steps.GIVEN_THAT_WE_REQUEST_PIKACHU();
        steps.THEN_ASSERT_RESPONSE_CODE_IS_200();
        steps.AND_ASSERT_RESPONSE_HAS_JSON_CONTENT_TYPE();
    }
}
