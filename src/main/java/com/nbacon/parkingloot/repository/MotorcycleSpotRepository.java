package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.model.park.MotorcycleSpot;
import com.nbacon.parkingloot.model.park.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotorcycleSpotRepository extends JpaRepository<MotorcycleSpot, Long> {
    List<MotorcycleSpot> findByOccupiedFalseAndParkingLot(ParkingLot parkingLot);
}
