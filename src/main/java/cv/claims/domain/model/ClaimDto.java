package cv.claims.domain.model;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record ClaimDto(
        Long id,
        @NonNull String claimId,
        @NonNull ClaimType type,
        @NonNull BigDecimal amount,
        @NonNull LocalDate deadline,
        @NonNull Complexity complexity,
        @NonNull LocalDateTime createdAt,
        ProcessStatus processStatus,
        LocalDateTime processedAt
) {}
