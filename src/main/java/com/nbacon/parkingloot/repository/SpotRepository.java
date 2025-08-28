package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long>, SpotRepositoryCustom, QuerydslPredicateExecutor<Spot> {
    List<Spot> findAllByVehicle(Vehicle vehicle);
}
