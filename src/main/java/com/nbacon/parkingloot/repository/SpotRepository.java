package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.model.park.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

}
