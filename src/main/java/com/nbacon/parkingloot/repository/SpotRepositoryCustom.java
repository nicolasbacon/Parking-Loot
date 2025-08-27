package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.repository.dto.ParkingLotInfos;

public interface SpotRepositoryCustom {
    ParkingLotInfos fetchParkingLotInfos(long parkingLotId);
}
