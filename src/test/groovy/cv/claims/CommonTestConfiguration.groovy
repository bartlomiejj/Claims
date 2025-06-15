package cv.claims

import org.awaitility.Awaitility
import org.awaitility.core.ConditionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

@Configuration
@Profile("test")
class CommonTestConfiguration {

    @Bean
    static Clock clock() {
        defaultClock()
    }

    static Clock defaultClock() {
        Clock.fixed(LocalDateTime.of(2025, 1, 1, 0, 0).toInstant(ZoneOffset.UTC), ZoneId.systemDefault())
    }

    static today() {
        LocalDateTime.now(defaultClock()).toLocalDate()
    }

    static now() {
        LocalDateTime.now(defaultClock())
    }

    static ConditionFactory waitUntil() {
        Awaitility.await()
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .atMost(10, TimeUnit.SECONDS)
    }
}
