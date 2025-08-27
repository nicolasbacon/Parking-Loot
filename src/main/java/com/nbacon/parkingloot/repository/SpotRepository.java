package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Spot s WHERE TYPE(s) = :type AND s.occupied = false AND s.parkingLot = :parkingLot ORDER BY s.position")
    List<Spot> findFreeSpotsByTypeOrderByPosition(@Param("type") Class<? extends Spot> type,
                                                  @Param("parkingLot") ParkingLot parkingLot);

}
