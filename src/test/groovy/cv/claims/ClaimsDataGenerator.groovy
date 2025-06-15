package cv.claims

import cv.claims.domain.model.ClaimDto
import cv.claims.domain.model.ClaimType
import cv.claims.domain.model.Complexity
import cv.claims.domain.model.ProcessStatus

import java.time.LocalDate

class ClaimsDataGenerator {

    static ClaimDto.ClaimDtoBuilder buildBasicDto(ProcessStatus status = ProcessStatus.UNPROCESSED,
                                                  BigDecimal amount = BigDecimal.TEN, LocalDate deadline = today()) {
        return ClaimDto.builder()
                .claimId(UUID.randomUUID().toString())
                .amount(amount)
                .deadline(deadline)
                .complexity(Complexity.MEDIUM)
                .type(ClaimType.MEDICAL)
                .createdAt(now())
                .processStatus(status)
                .processedAt(null)
    }

    static today() {
        CommonTestConfiguration.today()
    }

    static now() {
        CommonTestConfiguration.now()
    }
}
