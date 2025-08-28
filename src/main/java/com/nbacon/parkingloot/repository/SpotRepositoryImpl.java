package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.domain.model.park.QSpot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.QVehicle;
import com.nbacon.parkingloot.domain.model.vehicle.Van;
import com.nbacon.parkingloot.repository.dto.ParkingLotInfos;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class SpotRepositoryImpl implements SpotRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    private JPAQueryFactory queryFactory;

    public SpotRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ParkingLotInfos fetchParkingLotInfos(long parkingLotId) {
        QSpot spot = QSpot.spot;
        QVehicle vehicle = QVehicle.vehicle;

        NumberExpression<Long> totalCount = spot.count();
        NumberExpression<Long> freeCount = new CaseBuilder()
                .when(spot.occupied.isFalse()).then(1L)
                .otherwise(0L)
                .sum();
        NumberExpression<Long> occupiedCount = new CaseBuilder()
                .when(spot.occupied.isTrue()).then(1L)
                .otherwise(0L)
                .sum();
        NumberExpression<Long> vansAssignedCount = new CaseBuilder()
                .when(spot.occupied.isTrue().and(vehicle.instanceOf(Van.class)))
                .then(1L)
                .otherwise(0L)
                .sum();

        var result = queryFactory
                .select(totalCount, freeCount, occupiedCount, vansAssignedCount)
                .from(spot)
                .leftJoin(spot.vehicle, vehicle)
                .where(spot.parkingLot.id.eq(parkingLotId))
                .fetchOne();

        long total = result.get(totalCount);
        long free = result.get(freeCount) != null ? result.get(freeCount) : 0L;
        long occupied = result.get(occupiedCount) != null ? result.get(occupiedCount) : 0L;
        long vansAssigned = result.get(vansAssignedCount) != null ? result.get(vansAssignedCount) : 0L;

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