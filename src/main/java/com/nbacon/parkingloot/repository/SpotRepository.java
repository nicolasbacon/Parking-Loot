package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpotRepository extends JpaRepository<Spot, UUID>, SpotRepositoryCustom, QuerydslPredicateExecutor<Spot> {
    List<Spot> findAllByVehicle(Vehicle vehicle);
}
