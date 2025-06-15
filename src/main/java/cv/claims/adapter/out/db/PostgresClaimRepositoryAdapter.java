package cv.claims.adapter.out.db;

import cv.claims.domain.model.ClaimDto;
import cv.claims.domain.port.ClaimRepositoryPort;
import cv.claims.infrastructure.db.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostgresClaimRepositoryAdapter implements ClaimRepositoryPort {

    private final ClaimRepository claimRepository;

    @Override
    public void saveIncomingClaims(List<ClaimDto> claims) {
        claimRepository.saveIncomingClaims(claims);
    }

    @Override
    public List<ClaimDto> findAllUnprocessedClaimsToDate(LocalDate deadline) {
        return claimRepository.findAllUnprocessedClaimsToDate(deadline);
    }

    @Override
    public BigDecimal getProcessedAmountByDay(LocalDate date) {
        return claimRepository.getProcessedAmountByDay(date).orElse(BigDecimal.ZERO);
    }

    @Override
    public void markAsProcessed(List<ClaimDto> claims) {
        claimRepository.markAsProcessed(claims);
    }
}
