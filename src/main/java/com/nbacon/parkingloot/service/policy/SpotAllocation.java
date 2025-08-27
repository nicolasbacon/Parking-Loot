package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.domain.model.park.Spot;

import java.util.List;

public record SpotAllocation(List<Spot> spots) {
    public boolean isEmpty() {
        return spots == null || spots.isEmpty();
    }
}
