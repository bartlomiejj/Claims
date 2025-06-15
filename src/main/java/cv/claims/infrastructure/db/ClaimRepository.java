package cv.claims.infrastructure.db;

import cv.claims.domain.model.ClaimDto;
import cv.claims.domain.model.ClaimType;
import cv.claims.domain.model.Complexity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ClaimRepository {

    private static final int PAGE_SIZE = 100;
    private static final String DEADLINE_FIELD = "deadline";
    private static final Sort SORT_BY_DEADLINE = Sort.by(Sort.Direction.ASC, DEADLINE_FIELD);

    private final ClaimEntityRepository claimEntityRepository;
    private final Clock clock;

    public List<ClaimDto> saveIncomingClaims(List<ClaimDto> claims) {
        var entities = claims.stream()
                .map(this::fromDto)
                .toList();
        var distinctClaims = distinctByClaimId(entities);

        return claimEntityRepository.saveAllAndFlush(List.copyOf(distinctClaims)).stream()
                .map(this::fromEntity)
                .toList();
    }

    public List<ClaimDto> findAllUnprocessedClaimsToDate(LocalDate deadline) {
        var entities = claimEntityRepository.findAllByProcessStatusAndDeadlineBeforeEquals(
                cv.claims.infrastructure.db.ProcessStatus.UNPROCESSED,
                deadline,
                PageRequest.of(0, PAGE_SIZE, SORT_BY_DEADLINE));

        return entities.stream()
                .map(this::fromEntity)
                .toList();
    }

    public Optional<BigDecimal> getProcessedAmountByDay(LocalDate date) {
        return claimEntityRepository.getProcessedAmountByDay(date);
    }

    public void markAsProcessed(List<ClaimDto> claims) {
        var entities = claims.stream()
                .map(this::markAsProcessed)
                .map(this::fromDto)
                .toList();
        claimEntityRepository.saveAllAndFlush(entities);
    }

    private static Set<ClaimEntity> distinctByClaimId(List<ClaimEntity> entities) {
        return Set.copyOf(entities);
    }

    private ClaimDto markAsProcessed(ClaimDto dto) {
        return dto.toBuilder()
                .processedAt(LocalDateTime.now(clock))
                .processStatus(cv.claims.domain.model.ProcessStatus.PROCESSED)
                .build();
    }

    private ClaimEntity fromDto(ClaimDto dto) {
        return ClaimEntity.builder()
                .id(dto.id())
                .claimId(dto.claimId())
                .type(cv.claims.infrastructure.db.ClaimType.valueOf(dto.type().name()))
                .amount(dto.amount())
                .deadline(dto.deadline())
                .complexity(cv.claims.infrastructure.db.Complexity.valueOf(dto.complexity().name()))
                .createdAt(dto.createdAt())
                .processStatus(cv.claims.infrastructure.db.ProcessStatus.valueOf(dto.processStatus().name()))
                .processedAt(dto.processedAt())
                .build();
    }

    private ClaimDto fromEntity(ClaimEntity entity) {
        return ClaimDto.builder()
                .id(entity.getId())
                .claimId(entity.getClaimId())
                .type(ClaimType.valueOf(entity.getType().name()))
                .amount(entity.getAmount())
                .deadline(entity.getDeadline())
                .complexity(Complexity.valueOf(entity.getComplexity().name()))
                .createdAt(entity.getCreatedAt())
                .processStatus(cv.claims.domain.model.ProcessStatus.valueOf(entity.getProcessStatus().name()))
                .processedAt(entity.getProcessedAt())
                .build();
    }
}
