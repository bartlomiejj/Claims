package cv.claims.domain.service

import cv.claims.CommonTestConfiguration
import cv.claims.IntegrationTest
import cv.claims.adapter.in.csv.CsvProcessingService
import cv.claims.infrastructure.db.ClaimTestRepository
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared
import spock.util.time.MutableClock

import java.time.*
import java.time.temporal.ChronoUnit

class ClaimProcessingServiceIntegrationTest extends IntegrationTest {

    @Autowired
    CsvProcessingService csvProcessingService

    @Autowired
    ClaimTestRepository claimTestRepository

    @SpringBean
    Clock clock = new MutableClock(startInstant, ZoneId.systemDefault())

    @Autowired
    ClaimProcessingService sub

    @Shared
    def startInstant = Instant.parse("2025-06-01T00:00:00Z")
    @Shared
    def endInstant = Instant.parse("2025-06-30T00:00:00Z")

    def setup() {
        def csvFile = new File("src/test/resources/claims.csv")
        csvProcessingService.processCsv(csvFile.bytes)
    }

    def cleanup() {
        claimTestRepository.cleanup()
    }

    def "should process all claims with simulated days moving"() {
        given:
        def startDate = LocalDate.ofInstant(startInstant, ZoneId.systemDefault())
        def endDate = LocalDate.ofInstant(endInstant, ZoneId.systemDefault())

        when:
        while (startDate <= endDate) {
            sub.processClaims()
            clock + Duration.ofDays(1)
            startDate = startDate.plus(1, ChronoUnit.DAYS)
        }

        then:
        CommonTestConfiguration.waitUntil().untilAsserted(() -> {
            def claims = claimTestRepository.findAll()
            assert claims.size() == 50
            assert claims.every { it.processedAt != null }
            assert claims.every { it.processStatus.name() == "PROCESSED" }
        })
    }
}
