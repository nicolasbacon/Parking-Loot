package com.nbacon.parkingloot.repository;

import com.nbacon.parkingloot.domain.model.park.*;
import com.nbacon.parkingloot.domain.model.vehicle.Car;
import com.nbacon.parkingloot.domain.model.vehicle.Motorcycle;
import com.nbacon.parkingloot.domain.model.vehicle.Van;
import com.nbacon.parkingloot.domain.model.vehicle.Vehicle;
import com.nbacon.parkingloot.repository.dto.ParkingLotInfos;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class SpotRepositoryDataJpaTest {

    @Autowired
    SpotRepository spotRepository;

    @Autowired
    ParkingRepository parkingRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    EntityManager em;

    ParkingLot lotA;
    ParkingLot lotB;

    @BeforeEach
    void init() {
        lotA = ParkingLot.builder()
                .nbMotorcycleSpot(0).nbCarSpot(0).nbLargeSpot(0)
                .build();
        lotB = ParkingLot.builder()
                .nbMotorcycleSpot(0).nbCarSpot(0).nbLargeSpot(0)
                .build();
        parkingRepository.saveAll(List.of(lotA, lotB));

        em.flush();
    }

    private <T extends Spot> T addSpot(ParkingLot lot, T spot, int position, Vehicle vehicle) {
        spot.setParkingLot(lot);
        spot.setPosition(position);
        if (vehicle != null && vehicle.getId() == null) {
            em.persist(vehicle);
        }
        spot.setVehicle(vehicle);
        lot.getSpots().add(spot);
        em.persist(spot);
        return spot;
    }

    @Test
    void findFirstFreeSpotsByTypeOrderByPosition_returnsLowestFreePosition_forGivenLot() {
        addSpot(lotA, new CarSpot(), 0, new Car("CAR-A0"));
        addSpot(lotA, new CarSpot(), 1, null);
        addSpot(lotA, new CarSpot(), 2, new Car("CAR-A2"));
        addSpot(lotA, new CarSpot(), 3, null);
        addSpot(lotA, new CarSpot(), 4, null);

        addSpot(lotB, new CarSpot(), 0, null);
        addSpot(lotB, new CarSpot(), 1, null);

        em.flush();
        em.clear();

        Optional<Spot> firstFree = spotRepository.findFirstFreeSpotByType(CarSpot.class, lotA);

        assertTrue(firstFree.isPresent());
        assertEquals(1, firstFree.get().getPosition());
        assertEquals(lotA.getId(), firstFree.get().getParkingLot().getId());
    }

    @Test
    void findThreeConsecutiveBySpotType_returnsThreeConsecutiveFreeCarSpots_whenAvailable() {
        addSpot(lotA, new CarSpot(), 0, null);
        addSpot(lotA, new CarSpot(), 1, null);
        addSpot(lotA, new CarSpot(), 2, null);
        addSpot(lotA, new CarSpot(), 3, new Car("OCC-3"));
        addSpot(lotA, new CarSpot(), 4, null);
        addSpot(lotA, new CarSpot(), 5, null);
        addSpot(lotA, new CarSpot(), 6, new Car("OCC-6"));

        addSpot(lotB, new CarSpot(), 0, null);
        addSpot(lotB, new CarSpot(), 1, null);
        addSpot(lotB, new CarSpot(), 2, null);

        em.flush();
        em.clear();

        List<Spot> locked = spotRepository.findThreeConsecutiveFreeSpots(CarSpot.class, lotA);

        assertEquals(3, locked.size());
        assertEquals(List.of(0, 1, 2), locked.stream().map(Spot::getPosition).toList());
        assertTrue(locked.stream().allMatch(s -> s.getParkingLot().getId().equals(lotA.getId())));
    }

    @Test
    void findThreeConsecutiveBySpotType_returnsEmpty_whenNoThreeConsecutiveFree() {
        addSpot(lotA, new CarSpot(), 0, null);
        addSpot(lotA, new CarSpot(), 1, new Car("X1"));
        addSpot(lotA, new CarSpot(), 2, null);
        addSpot(lotA, new CarSpot(), 3, null);
        addSpot(lotA, new CarSpot(), 4, new Car("X4"));

        em.flush();
        em.clear();

        List<Spot> locked = spotRepository.findThreeConsecutiveFreeSpots(CarSpot.class, lotA);

        assertTrue(locked.isEmpty());
    }

    @Test
    void fetchParkingLotInfos_computesCounts_full_empty_fullyAssignedTypes_andVanAssignments() {
        Vehicle moto = new Motorcycle("M-1");
        Vehicle car = new Car("C-1");
        Vehicle van = new Van("V-1");
        vehicleRepository.saveAll(List.of(moto, car, van));

        addSpot(lotA, new MotorcycleSpot(), 0, moto);
        addSpot(lotA, new MotorcycleSpot(), 1, null);

        addSpot(lotA, new CarSpot(), 0, van);
        addSpot(lotA, new CarSpot(), 1, car);

        addSpot(lotA, new LargeSpot(), 0, null);

        em.flush();
        em.clear();

        ParkingLotInfos infos = spotRepository.fetchParkingLotInfos(lotA.getId());

        assertEquals(2, infos.nbSpotRemaining());
        assertEquals(5, infos.totalNbSpot());
        assertFalse(infos.isFull());
        assertFalse(infos.isEmpty());

        assertTrue(infos.typesOfSeatsFullyAssigned().contains("CarSpot"));
        assertEquals(1, infos.numberOfSpotsVansAssigned());
    }

    @Test
    void findAllByVehicle_returnsAllSpotsBoundToVehicle() {
        Vehicle car = new Car("CAR-XYZ");
        vehicleRepository.save(car);

        Spot s1 = addSpot(lotA, new CarSpot(), 0, car);
        Spot s2 = addSpot(lotA, new LargeSpot(), 0, car);
        addSpot(lotA, new CarSpot(), 1, null); // libre, ne doit pas apparaître

        em.flush();
        em.clear();

        List<Spot> spots = spotRepository.findAllByVehicle(car);

        assertEquals(2, spots.size());
        assertTrue(spots.stream().anyMatch(s -> s.getId().equals(s1.getId())));
        assertTrue(spots.stream().anyMatch(s -> s.getId().equals(s2.getId())));
    }
}
