package cv.claims.domain.strategy

import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import cv.claims.ClaimsDataGenerator
import cv.claims.CommonTestConfiguration
import cv.claims.domain.model.Complexity
import spock.lang.Specification

class VehicleClaimStrategyTest extends Specification {

    @Subject
    VehicleClaimStrategy sub

    def "should sort claims"() {
        given:
        def claim1 = ClaimsDataGenerator.buildBasicDto().complexity(Complexity.HIGH).deadline(today()).amount(BigDecimal.ONE).build()
        def claim2 = ClaimsDataGenerator.buildBasicDto().complexity(Complexity.HIGH).deadline(today()).amount(BigDecimal.TWO).build()
        def claim3 = ClaimsDataGenerator.buildBasicDto().complexity(Complexity.LOW).deadline(today().minusDays(1)).build()
        def claim4 = ClaimsDataGenerator.buildBasicDto().complexity(Complexity.MEDIUM).deadline(today().plusDays(1)).build()
        def claims = [claim1, claim2, claim3, claim4]

        when:
        def sortedClaims = sub.sort(claims)

        then:
        sortedClaims[0].claimId() == claim2.claimId()
        sortedClaims[1].claimId() == claim1.claimId()
        sortedClaims[2].claimId() == claim3.claimId()
        sortedClaims[3].claimId() == claim4.claimId()
    }

    private static today() {
        CommonTestConfiguration.today()
    }
}
