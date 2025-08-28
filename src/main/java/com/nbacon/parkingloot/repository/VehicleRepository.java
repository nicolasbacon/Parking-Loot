package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.domain.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Vehicle findFirstByLicensePlate(String licensePlate);
}
