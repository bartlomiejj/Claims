package cv.claims.domain.strategy;

import cv.claims.domain.model.ClaimType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
class ClaimStrategyConfig {

    @Bean
    Map<ClaimType, ClaimProcessingStrategy> claimProcessingStrategies(MedicalClaimStrategy medicalClaimStrategy,
                                                                           VehicleClaimStrategy vehicleClaimStrategy,
                                                                           PropertyClaimStrategy propertyClaimStrategy) {
        var map = new LinkedHashMap<ClaimType, ClaimProcessingStrategy>();
        map.put(ClaimType.MEDICAL, medicalClaimStrategy);
        map.put(ClaimType.VEHICLE, vehicleClaimStrategy);
        map.put(ClaimType.PROPERTY, propertyClaimStrategy);
        return map;
    }
}
