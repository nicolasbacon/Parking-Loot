package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.VehicleType;

import java.util.List;
import java.util.Optional;

public interface SpotSelectionPolicy {
    VehicleType supportedType();

    Optional<List<Spot>> selectAllocation(ParkingLot parkingLot);

}
