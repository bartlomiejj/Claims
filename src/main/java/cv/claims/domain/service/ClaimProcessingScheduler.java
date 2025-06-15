package cv.claims.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClaimProcessingScheduler {

    private final ClaimProcessingService service;

//    @Scheduled(cron = "2 0 0 * * *") // Uncomment this line to run the job at midnight every day
    @Scheduled(fixedDelay = 60000) // For testing purposes, runs every minute
    public void runClaimProcessing() {
        log.info("Starting claim processing job");
        service.processClaims();
        log.info("Claim processing job completed");
    }
}
