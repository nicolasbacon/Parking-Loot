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
import java.util.Optional;
import java.util.UUID;

@Repository
class SpotRepositoryImpl implements SpotRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory queryFactory;

    public SpotRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ParkingLotInfos fetchParkingLotInfos(UUID parkingLotId) {
        QSpot spot = QSpot.spot;
        QVehicle vehicle = QVehicle.vehicle;

        NumberExpression<Long> totalCount = spot.count();
        NumberExpression<Long> freeCount = new CaseBuilder()
                .when(spot.vehicle.isNull()).then(1L)
                .otherwise(0L)
                .sum();
        NumberExpression<Long> occupiedCount = new CaseBuilder()
                .when(spot.vehicle.isNotNull()).then(1L)
                .otherwise(0L)
                .sum();
        NumberExpression<Long> vansAssignedCount = new CaseBuilder()
                .when(spot.vehicle.isNotNull().and(vehicle.instanceOf(Van.class)))
                .then(1L)
                .otherwise(0L)
                .sum();

        var result = queryFactory
                .select(totalCount, freeCount, occupiedCount, vansAssignedCount)
                .from(spot)
                .leftJoin(spot.vehicle, vehicle)
                .where(spot.parkingLot.id.eq(parkingLotId))
                .fetchOne();

        assert result != null;
        long total = Optional.ofNullable(result.get(totalCount)).orElse(0L);
        long free = Optional.ofNullable(result.get(freeCount)).orElse(0L);
        long occupied = Optional.ofNullable(result.get(occupiedCount)).orElse(0L);
        long vansAssigned = Optional.ofNullable(result.get(vansAssignedCount)).orElse(0L);

        @SuppressWarnings("unchecked")
        List<Class<? extends Spot>> fullyAssignedTypes = em.createQuery("""
                            select type(s)
                            from Spot s
                            where s.parkingLot.id = :parkingLotId
                            group by type(s)
                            having coalesce(sum(case when s.vehicle IS NULL then 1 else 0 end), 0) = 0
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