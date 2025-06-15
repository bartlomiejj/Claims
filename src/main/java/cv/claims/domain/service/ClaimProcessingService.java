package cv.claims.domain.service;

import cv.claims.domain.model.ClaimDto;
import cv.claims.domain.model.ClaimType;
import cv.claims.domain.model.Complexity;
import cv.claims.domain.port.ClaimRepositoryPort;
import cv.claims.domain.strategy.ClaimStrategyResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ClaimProcessingService {

    private final ClaimRepositoryPort repositoryPort;
    private final ClaimStrategyResolver strategyResolver;
    private final Clock clock;

    @Value("${claims.maxDailyBudget:15000}")
    private BigDecimal maxDailyBudget;

    @Value("${claims.maxDailyHighProcessingCount:2}")
    private Integer maxDailyHighProcessingCount;

    @Transactional
    public void processClaims() {
        var today = LocalDate.now(clock);
        var claims = repositoryPort.findAllUnprocessedClaimsToDate(today);

        if (claims.isEmpty()) {
            log.info("No unprocessed claims found.");
            return;
        }

        var processedAmountToday = repositoryPort.getProcessedAmountByDay(today); // failover solution so process can be started few times a day
        if (processedAmountToday.compareTo(maxDailyBudget) >= 0) {
            log.info("Today's budget already exhausted. No claims will be processed.");
            return;
        }

        List<ClaimDto> queue = new ArrayList<>(claims);

        while (!queue.isEmpty()) {
            BigDecimal budget = maxDailyBudget.subtract(processedAmountToday);
            int highCount = 0;

            List<ClaimDto> toProcessToday = new ArrayList<>();
            List<ClaimDto> toPostpone = new ArrayList<>();

            List<ClaimDto> toProcess = new ArrayList<>();

            for (ClaimType type : ClaimType.values()) {
                List<ClaimDto> typeClaims = queue.stream()
                        .filter(c -> c.type() == type)
                        .toList();
                toProcess.addAll(strategyResolver.getStrategy(type).sort(typeClaims));
            }

            for (ClaimDto claim : toProcess) {
                if (!queue.remove(claim)) {
                    continue;
                }

                var isHigh = Complexity.HIGH == claim.complexity();
                if (isHigh && highCount >= maxDailyHighProcessingCount || budget.compareTo(claim.amount()) < 0) {
                    toPostpone.add(claim);
                    continue;
                }

                if (isHigh) {
                    highCount++;
                }

                budget = budget.subtract(claim.amount());

                toProcessToday.add(claim);
            }

            releasePayments(toProcessToday);
            printDailySummary(budget, highCount, toProcessToday, toPostpone);
        }
    }

    private void releasePayments(List<ClaimDto> claims) {
        claims.forEach(this::releasePayment);
        repositoryPort.markAsProcessed(claims);
    }

    private void releasePayment(ClaimDto claim) {
        // Logic to release payment for the claim
        log.info("Releasing payment for claim: {}", claim.claimId());
    }

    private void printDailySummary(BigDecimal budget, int highCount, List<ClaimDto> today, List<ClaimDto> postponed) {
        log.info("Daily Summary for: {}", LocalDate.now(clock));
        log.info("Remaining daily budget: {} of total: {}", budget, maxDailyBudget);
        log.info("High complexity claims processed today: {}", highCount);
        log.info("Claims processed today: {}", today.size());
        today.forEach(claim -> log.info(" - Claim ID: {}, Type: {}, Complexity: {}, Amount: {}, Deadline: {}", claim.type(), claim.complexity(), claim.claimId(), claim.amount(), claim.deadline()));
        log.info("Claims postponed to next day: {}", postponed.size());
        postponed.forEach(claim -> log.info(" - Claim ID: {}, Type: {}, Complexity: {}, Amount: {}, Deadline: {}", claim.claimId(), claim.type(), claim.complexity(), claim.amount(), claim.deadline()));
    }
}

