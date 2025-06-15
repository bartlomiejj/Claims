package cv.claims.domain.strategy

import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import cv.claims.ClaimsDataGenerator
import cv.claims.CommonTestConfiguration
import spock.lang.Specification

class PropertyClaimStrategyTest extends Specification {

    @Subject
    PropertyClaimStrategy sub

    def "should sort claims"() {
        given:
        def claim1 = ClaimsDataGenerator.buildBasicDto().deadline(today()).amount(BigDecimal.ONE).build()
        def claim2 = ClaimsDataGenerator.buildBasicDto().deadline(today()).amount(BigDecimal.TWO).build()
        def claim3 = ClaimsDataGenerator.buildBasicDto().deadline(today().minusDays(1)).build()
        def claim4 = ClaimsDataGenerator.buildBasicDto().deadline(today().plusDays(1)).build()
        def claims = [claim1, claim2, claim3, claim4]

        when:
        def sortedClaims = sub.sort(claims)

        then:
        sortedClaims[0].claimId() == claim3.claimId()
        sortedClaims[1].claimId() == claim4.claimId()
        sortedClaims[2].claimId() == claim2.claimId()
        sortedClaims[3].claimId() == claim1.claimId()
    }

    private static today() {
        CommonTestConfiguration.today()
    }
}
