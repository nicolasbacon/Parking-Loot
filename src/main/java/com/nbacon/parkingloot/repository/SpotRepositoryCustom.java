package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.repository.dto.ParkingLotInfos;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpotRepositoryCustom {
    Optional<Spot> findFirstFreeSpotByType(Class<? extends Spot> spotType, ParkingLot parkingLot);

    List<Spot> findThreeConsecutiveFreeSpots(Class<? extends Spot> spotType, ParkingLot parkingLot);
    ParkingLotInfos fetchParkingLotInfos(UUID parkingLotId);
}