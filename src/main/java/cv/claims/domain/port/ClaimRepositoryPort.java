package cv.claims.domain.port;

import cv.claims.domain.model.ClaimDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ClaimRepositoryPort {
    void saveIncomingClaims(List<ClaimDto> claims);
    List<ClaimDto> findAllUnprocessedClaimsToDate(LocalDate deadline);
    BigDecimal getProcessedAmountByDay(LocalDate date);
    void markAsProcessed(List<ClaimDto> claims);}
