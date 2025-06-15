package cv.claims.domain.service

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import cv.claims.ClaimsDataGenerator
import cv.claims.CommonTestConfiguration
import cv.claims.domain.model.ClaimType
import cv.claims.domain.model.Complexity
import cv.claims.domain.port.ClaimRepositoryPort
import cv.claims.domain.strategy.*
import spock.lang.Specification

import java.time.Clock

class ClaimProcessingServiceTest extends Specification {

    @Subject
    ClaimProcessingService sub

    @Collaborator
    ClaimRepositoryPort repositoryPort = Mock()

    @Collaborator
    ClaimStrategyResolver strategyResolver = Mock()

    @Collaborator
    ClaimProcessingStrategy medicalClaimStrategy = Mock(MedicalClaimStrategy.class)

    @Collaborator
    ClaimProcessingStrategy propertyClaimStrategy = Mock(PropertyClaimStrategy.class)

    @Collaborator
    ClaimProcessingStrategy vehicleClaimStrategy = Mock(VehicleClaimStrategy.class)

    @Collaborator
    Clock clock = CommonTestConfiguration.defaultClock()

    def setup() {
        sub.maxDailyBudget = 100
        sub.maxDailyHighProcessingCount = 2
    }

    def "should do nothing when no claims to process"() {
        given:
        def claims = List.of()
        repositoryPort.findAllUnprocessedClaimsToDate(today()) >> claims

        when:
        sub.processClaims()

        then:
        0 * repositoryPort.markAsProcessed(_)
    }

    def "should do nothing when daily budget exhausted"() {
        given:
        def claims = List.of(ClaimsDataGenerator.buildBasicDto())
        repositoryPort.findAllUnprocessedClaimsToDate(today()) >> claims
        repositoryPort.getProcessedAmountByDay(_) >> sub.maxDailyBudget

        when:
        sub.processClaims()

        then:
        0 * repositoryPort.markAsProcessed(_)
    }

    def "should process claims with HIGH complexity when limit allows it"() {
        def claim1 = ClaimsDataGenerator.buildBasicDto().complexity(Complexity.HIGH).build()
        def claim2 = ClaimsDataGenerator.buildBasicDto().complexity(Complexity.HIGH).build()
        def claim3 = ClaimsDataGenerator.buildBasicDto().complexity(Complexity.HIGH).build()
        def claims = List.of(claim1, claim2, claim3)
        repositoryPort.findAllUnprocessedClaimsToDate(today()) >> claims
        repositoryPort.getProcessedAmountByDay(_) >> 0
        strategyResolver.getStrategy(ClaimType.MEDICAL) >> medicalClaimStrategy
        strategyResolver.getStrategy(ClaimType.VEHICLE) >> vehicleClaimStrategy
        strategyResolver.getStrategy(ClaimType.PROPERTY) >> propertyClaimStrategy
        medicalClaimStrategy.sort(_) >> [claim1]
        vehicleClaimStrategy.sort(_) >> [claim2]
        propertyClaimStrategy.sort(_) >> [claim3]

        when:
        sub.processClaims()

        then:
        1 * repositoryPort.markAsProcessed({
            assert it.size() == 2
        })
    }

    def "should process claims when budget and processing count allow it"() {
        given:
        def claim1 = ClaimsDataGenerator.buildBasicDto().type(ClaimType.MEDICAL).build()
        def claim2 = ClaimsDataGenerator.buildBasicDto().type(ClaimType.VEHICLE).build()
        def claim3 = ClaimsDataGenerator.buildBasicDto().type(ClaimType.PROPERTY).build()
        def claims = List.of(claim1, claim2, claim3)
        repositoryPort.findAllUnprocessedClaimsToDate(today()) >> claims
        repositoryPort.getProcessedAmountByDay(_) >> 0
        strategyResolver.getStrategy(ClaimType.MEDICAL) >> medicalClaimStrategy
        strategyResolver.getStrategy(ClaimType.VEHICLE) >> vehicleClaimStrategy
        strategyResolver.getStrategy(ClaimType.PROPERTY) >> propertyClaimStrategy
        medicalClaimStrategy.sort(_) >> [claim1]
        vehicleClaimStrategy.sort(_) >> [claim2]
        propertyClaimStrategy.sort(_) >> [claim3]

        when:
        sub.processClaims()

        then:
        1 * repositoryPort.markAsProcessed({
            assert it.size() == 3
        })
    }

    private static today() {
        CommonTestConfiguration.today()
    }
}
