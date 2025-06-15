package cv.claims.domain.strategy;

import cv.claims.domain.model.ClaimDto;

import java.util.List;

public interface ClaimProcessingStrategy {
    List<ClaimDto> sort(List<ClaimDto> claims);
}
