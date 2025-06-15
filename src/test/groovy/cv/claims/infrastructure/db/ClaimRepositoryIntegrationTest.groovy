package cv.claims.infrastructure.db

import cv.claims.ClaimsDataGenerator
import cv.claims.CommonTestConfiguration
import cv.claims.IntegrationTest
import org.springframework.beans.factory.annotation.Autowired

class ClaimRepositoryIntegrationTest extends IntegrationTest {

    @Autowired
    ClaimRepository claimRepository

    @Autowired
    ClaimTestRepository claimTestRepository

    def cleanup() {
        claimTestRepository.cleanup()
    }

    def "should save distinct claims"() {
        given:
        def id = UUID.randomUUID().toString()
        def claim1 = ClaimsDataGenerator.buildBasicDto().claimId(id).build()
        def claim2 = ClaimsDataGenerator.buildBasicDto().claimId(id).build()

        when:
        claimRepository.saveIncomingClaims([claim1, claim2])

        then:
        def claims = claimTestRepository.findAll()
        claims.size() == 1
    }

    def "should find all unprocessed claims to date sorted by deadline"() {
        given:
        def claim1 = ClaimsDataGenerator.buildBasicDto().deadline(today()).build()
        def claim2 = ClaimsDataGenerator.buildBasicDto().deadline(today().plusDays(1)).build()
        def claim3 = ClaimsDataGenerator.buildBasicDto().processStatus(cv.claims.domain.model.ProcessStatus.PROCESSED).deadline(today().minusDays(1)).build()
        def claim4 = ClaimsDataGenerator.buildBasicDto().deadline(today().minusDays(1)).build()
        claimRepository.saveIncomingClaims([claim1, claim2, claim3, claim4])

        when:
        def claims = claimRepository.findAllUnprocessedClaimsToDate(today())

        then:
        claims.size() == 2
        claims[0].deadline() == claim4.deadline()
        claims[1].deadline() == claim1.deadline()
    }

    def "should sum claims amounts processed in date"() {
        given:
        def claim1 = ClaimsDataGenerator.buildBasicDto().amount(BigDecimal.TEN).processedAt(now()).build()
        def claim2 = ClaimsDataGenerator.buildBasicDto().amount(BigDecimal.TEN).processedAt(now()).build()
        def claim3 = ClaimsDataGenerator.buildBasicDto().amount(BigDecimal.TEN).processedAt(now().plusDays(1)).build()
        claimRepository.saveIncomingClaims([claim1, claim2, claim3])

        when:
        def sum = claimRepository.getProcessedAmountByDay(today())

        then:
        sum.get() == claim1.amount() + claim2.amount()
    }

    def "should mark claim as processed"() {
        given:
        def claim = ClaimsDataGenerator.buildBasicDto().build()
        def claims = List.of(claim)
        def savedClaims = claimRepository.saveIncomingClaims(claims)

        when:
        claimRepository.markAsProcessed(savedClaims)

        then:
        def toProcess = claimRepository.findAllUnprocessedClaimsToDate(today())
        toProcess.isEmpty()
    }

    private static today() {
        CommonTestConfiguration.today()
    }

    private static now() {
        CommonTestConfiguration.now()
    }
}
