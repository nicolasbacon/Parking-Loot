package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.dto.request.VehicleType;

import java.util.Optional;

public interface SpotSelectionPolicy {
    VehicleType supportedType();

    Optional<SpotAllocation> selectAllocation(ParkingLot parkingLot);

}
