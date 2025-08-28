package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.repository.dto.ParkingLotInfos;

import java.util.UUID;

public interface SpotRepositoryCustom {
    ParkingLotInfos fetchParkingLotInfos(UUID parkingLotId);
}
