package com.eventhub.event;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test to verify that the Spring application context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
class EventServiceApplicationTests {

    @Test
    void contextLoads() {
        // The context loading itself is the assertion.
    }
}
