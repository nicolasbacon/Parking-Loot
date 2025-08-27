package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.domain.model.vehicle.VehicleType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class SpotSelectionRegistry {
    private final Map<VehicleType, SpotSelectionPolicy> policiesByType;

    public SpotSelectionRegistry(List<SpotSelectionPolicy> policies) {
        this.policiesByType = new EnumMap<>(VehicleType.class);
        for (SpotSelectionPolicy p : policies) {
            this.policiesByType.put(p.supportedType(), p);
        }
    }

    public SpotSelectionPolicy getPolicy(VehicleType type) {
        SpotSelectionPolicy policy = policiesByType.get(type);
        if (policy == null) {
            throw new IllegalArgumentException("No SpotSelectionPolicy for type: " + type);
        }
        return policy;
    }

}
