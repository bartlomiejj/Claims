package cv.claims.domain.port;

import cv.claims.domain.model.ClaimDto;

import java.util.List;

public interface ClaimInputPort {
    void loadClaims(List<ClaimDto> claims);
}
