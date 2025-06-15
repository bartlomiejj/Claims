package cv.claims.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum ClaimType {
    MEDICAL(1), VEHICLE(2), PROPERTY(3);

    private final int order;

    public static List<ClaimType> getAllOrdered() {
        return Arrays.stream(values()).sorted(Comparator.comparing(ClaimType::getOrder)).toList();
    }
}
