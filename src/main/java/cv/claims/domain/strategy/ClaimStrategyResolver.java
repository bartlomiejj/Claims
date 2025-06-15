package cv.claims.domain.strategy;

import cv.claims.domain.model.ClaimType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClaimStrategyResolver {

    private final Map<ClaimType, ClaimProcessingStrategy> claimProcessingStrategies;

    public ClaimProcessingStrategy getStrategy(ClaimType type) {
        return claimProcessingStrategies.get(type);
    }
}
