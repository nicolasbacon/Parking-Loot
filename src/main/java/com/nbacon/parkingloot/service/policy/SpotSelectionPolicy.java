package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.dto.request.VehicleType;
import com.nbacon.parkingloot.model.park.ParkingLot;

import java.util.Optional;

public interface SpotSelectionPolicy {
    VehicleType supportedType();

    Optional<SpotAllocation> selectAllocation(ParkingLot parkingLot);

}
