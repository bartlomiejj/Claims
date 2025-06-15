package cv.claims.domain.strategy;

import cv.claims.domain.model.ClaimDto;
import cv.claims.domain.model.Complexity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
class VehicleClaimStrategy implements ClaimProcessingStrategy {

    @Override
    public List<ClaimDto> sort(List<ClaimDto> claims) {
        return claims.stream()
                .sorted(Comparator
                        .comparing((ClaimDto c) -> c.complexity() == Complexity.HIGH ? 0 : 1)
                        .thenComparing(ClaimDto::deadline).reversed()
                        .thenComparing(ClaimDto::amount).reversed())
                .toList();
    }
}
