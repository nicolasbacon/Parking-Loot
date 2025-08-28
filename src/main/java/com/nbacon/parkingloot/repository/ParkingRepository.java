package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParkingRepository extends JpaRepository<ParkingLot, UUID> {

}
