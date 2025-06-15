package cv.claims.domain.strategy;

import cv.claims.domain.model.ClaimDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
class PropertyClaimStrategy implements ClaimProcessingStrategy {

    @Override
    public List<ClaimDto> sort(List<ClaimDto> claims) {
        return claims.stream()
                .sorted(Comparator.comparing(ClaimDto::amount).reversed().thenComparing(ClaimDto::deadline))
                .toList();
    }
}
