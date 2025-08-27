package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.Van;
import com.nbacon.parkingloot.repository.dto.ParkingLotInfos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class SpotRepositoryImpl implements SpotRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public ParkingLotInfos fetchParkingLotInfos(long parkingLotId) {
        Object[] agg = em.createQuery("""
                            select
                                           count(s),
                                           coalesce(sum(case when s.occupied = false then 1 else 0 end), 0),
                                           coalesce(sum(case when s.occupied = true then 1 else 0 end), 0),
                                           coalesce(sum(case when s.occupied = true and type(v) = :vanType then 1 else 0 end), 0)
                                        from Spot s
                                        left join s.vehicle v
                                        where s.parkingLot.id = :parkingLotId
                        """, Object[].class)
                .setParameter("parkingLotId", parkingLotId)
                .setParameter("vanType", Van.class)
                .getSingleResult();

        long total = ((Number) agg[0]).longValue();
        long free = agg[1] == null ? 0L : ((Number) agg[1]).longValue();
        long occupied = agg[2] == null ? 0L : ((Number) agg[2]).longValue();
        long vansAssigned = agg[3] == null ? 0L : ((Number) agg[3]).longValue();


        @SuppressWarnings("unchecked")
        List<Class<? extends Spot>> fullyAssignedTypes = em.createQuery("""
                            select type(s)
                            from Spot s
                            where s.parkingLot.id = :parkingLotId
                            group by type(s)
                            having coalesce(sum(case when s.occupied = false then 1 else 0 end), 0) = 0
                        """)
                .setParameter("parkingLotId", parkingLotId)
                .getResultList();

        List<String> typesFullyAssigned = fullyAssignedTypes.stream()
                .map(Class::getSimpleName)
                .toList();

        boolean isFull = free == 0 && total > 0;
        boolean isEmpty = occupied == 0 && total > 0;

        return new ParkingLotInfos(
                (int) free,
                (int) total,
                isFull,
                isEmpty,
                typesFullyAssigned,
                (int) vansAssigned
        );
    }
}