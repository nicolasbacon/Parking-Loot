package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.Vehicle;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long>, SpotRepositoryCustom {

    private static String resolveDiscriminator(Class<? extends Spot> type) {
        DiscriminatorValue dv = type.getAnnotation(DiscriminatorValue.class);
        if (dv != null && dv.value() != null && !dv.value().isBlank()) {
            return dv.value();
        }
        return type.getSimpleName();
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Spot s WHERE TYPE(s) = :type AND s.occupied = false AND s.parkingLot = :parkingLot ORDER BY s.position LIMIT 1")
    List<Spot> findFreeSpotsByTypeOrderByPosition(@Param("type") Class<? extends Spot> type,
                                                  @Param("parkingLot") ParkingLot parkingLot,
                                                  Pageable pageable);

    default Optional<Spot> findFirstFreeSpotsByTypeOrderByPosition(Class<? extends Spot> type,
                                                                   ParkingLot parkingLot) {
        return findFreeSpotsByTypeOrderByPosition(type, parkingLot, PageRequest.of(0, 1))
                .stream().findFirst();
    }

    @Query(value = """
            WITH start AS (
                SELECT s1.position AS p0
                FROM spot s1
                JOIN spot s2
                  ON s2.parking_lot_id = s1.parking_lot_id
                 AND s2.position = s1.position + 1
                 AND s2.occupied = false
                 AND s2.spot_type = :spotType
                JOIN spot s3
                  ON s3.parking_lot_id = s1.parking_lot_id
                 AND s3.position = s1.position + 2
                 AND s3.occupied = false
                 AND s3.spot_type = :spotType
                WHERE s1.parking_lot_id = :parkingLotId
                  AND s1.occupied = false
                  AND s1.spot_type = :spotType
                ORDER BY s1.position
                LIMIT 1
            )
            SELECT s.*
            FROM spot s
            JOIN start st ON s.position IN (st.p0, st.p0 + 1, st.p0 + 2)
            WHERE s.parking_lot_id = :parkingLotId
              AND s.spot_type = :spotType
            ORDER BY s.position
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<Spot> lockThreeConsecutiveBySpotType(@Param("parkingLotId") long parkingLotId,
                                              @Param("spotType") String spotType);

    default List<Spot> lockThreeConsecutiveBySpotType(long parkingLotId, Class<? extends Spot> type) {
        return lockThreeConsecutiveBySpotType(parkingLotId, resolveDiscriminator(type));
    }

    List<Spot> findAllByVehicle(Vehicle vehicle);
}
