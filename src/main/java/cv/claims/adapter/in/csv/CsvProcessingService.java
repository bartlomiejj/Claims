package cv.claims.adapter.in.csv;


import cv.claims.domain.model.ClaimDto;
import cv.claims.domain.model.ClaimType;
import cv.claims.domain.model.Complexity;
import cv.claims.domain.model.ProcessStatus;
import cv.claims.domain.port.ClaimInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
class CsvProcessingService {

    private final ClaimInputPort claimInputPort;
    private final Clock clock;

    public void processCsv(byte[] csvData) {
        try (var reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(csvData)))) {
            var claims = reader.lines().skip(1).map(line -> {
                String[] parts = line.split(",");
                return ClaimDto.builder()
                        .claimId(parts[0])
                        .type(ClaimType.valueOf(parts[1]))
                        .amount(new BigDecimal(parts[2]))
                        .deadline(LocalDate.parse(parts[3]))
                        .complexity(Complexity.valueOf(parts[4]))
                        .processStatus(ProcessStatus.UNPROCESSED)
                        .createdAt(LocalDateTime.now(clock))
                        .build();
            }).toList();

            claimInputPort.loadClaims(claims);
            log.info("Processed {} claims from CSV", claims.size());
        } catch (Exception e) {
            throw new RuntimeException("CSV read error", e);
        }
    }
}
