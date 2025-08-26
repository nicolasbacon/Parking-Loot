package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.model.park.CarSpot;
import com.nbacon.parkingloot.model.park.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarSpotRepository extends JpaRepository<CarSpot, Long> {
    List<CarSpot> findByOccupiedFalseAndParkingLot(ParkingLot parkingLot);
}
