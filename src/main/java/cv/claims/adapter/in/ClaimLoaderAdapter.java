package cv.claims.adapter.in;

import cv.claims.domain.model.ClaimDto;
import cv.claims.domain.port.ClaimInputPort;
import cv.claims.domain.port.ClaimRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
class ClaimLoaderAdapter implements ClaimInputPort {

    private final ClaimRepositoryPort repository;

    @Override
    public void loadClaims(List<ClaimDto> claims) {
        repository.saveIncomingClaims(claims);
    }
}
