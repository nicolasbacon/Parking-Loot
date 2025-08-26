package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.model.park.LargeSpot;
import com.nbacon.parkingloot.model.park.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LargeSpotRepository extends JpaRepository<LargeSpot, Long> {
    List<LargeSpot> findByOccupiedFalseAndParkingLot(ParkingLot parkingLot);
}
