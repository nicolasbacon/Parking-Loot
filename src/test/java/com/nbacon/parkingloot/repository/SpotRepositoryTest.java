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

    private <T extends Spot> T addSpot(ParkingLot lot, T spot, int position, boolean occupied, Vehicle vehicle) {
        spot.setParkingLot(lot);
        spot.setPosition(position);
        spot.setOccupied(occupied);
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
        addSpot(lotA, new CarSpot(), 0, true, new Car("CAR-A0"));
        addSpot(lotA, new CarSpot(), 1, false, null);
        addSpot(lotA, new CarSpot(), 2, true, new Car("CAR-A2"));
        addSpot(lotA, new CarSpot(), 3, false, null);
        addSpot(lotA, new CarSpot(), 4, false, null);

        addSpot(lotB, new CarSpot(), 0, false, null);
        addSpot(lotB, new CarSpot(), 1, false, null);

        em.flush();
        em.clear();

        Optional<Spot> firstFree = spotRepository.findFirstFreeSpotsByTypeOrderByPosition(CarSpot.class, lotA);

        assertTrue(firstFree.isPresent(), "Une place libre devrait être trouvée");
        assertEquals(1, firstFree.get().getPosition());
        assertEquals(lotA.getId(), firstFree.get().getParkingLot().getId());
    }

    @Test
    void lockThreeConsecutiveBySpotType_returnsThreeConsecutiveFreeCarSpots_whenAvailable() {
        addSpot(lotA, new CarSpot(), 0, false, null);
        addSpot(lotA, new CarSpot(), 1, false, null);
        addSpot(lotA, new CarSpot(), 2, false, null);
        addSpot(lotA, new CarSpot(), 3, true, new Car("OCC-3"));
        addSpot(lotA, new CarSpot(), 4, false, null);
        addSpot(lotA, new CarSpot(), 5, false, null);
        addSpot(lotA, new CarSpot(), 6, true, new Car("OCC-6"));

        addSpot(lotB, new CarSpot(), 0, false, null);
        addSpot(lotB, new CarSpot(), 1, false, null);
        addSpot(lotB, new CarSpot(), 2, false, null);

        em.flush();
        em.clear();

        List<Spot> locked = spotRepository.lockThreeConsecutiveBySpotType(lotA.getId(), CarSpot.class);

        assertEquals(3, locked.size(), "Devrait retourner 3 places consécutives");
        assertEquals(List.of(0, 1, 2), locked.stream().map(Spot::getPosition).toList());
        assertTrue(locked.stream().allMatch(s -> s.getParkingLot().getId().equals(lotA.getId())));
    }

    @Test
    void lockThreeConsecutiveBySpotType_returnsEmpty_whenNoThreeConsecutiveFree() {
        addSpot(lotA, new CarSpot(), 0, false, null);
        addSpot(lotA, new CarSpot(), 1, true, new Car("X1"));
        addSpot(lotA, new CarSpot(), 2, false, null);
        addSpot(lotA, new CarSpot(), 3, false, null);
        addSpot(lotA, new CarSpot(), 4, true, new Car("X4"));

        em.flush();
        em.clear();

        List<Spot> locked = spotRepository.lockThreeConsecutiveBySpotType(lotA.getId(), CarSpot.class);

        assertTrue(locked.isEmpty(), "Aucune séquence de 3 consécutifs libres ne doit être trouvée");
    }

    @Test
    void fetchParkingLotInfos_computesCounts_full_empty_fullyAssignedTypes_andVanAssignments() {
        Vehicle moto = new Motorcycle("M-1");
        Vehicle car = new Car("C-1");
        Vehicle van = new Van("V-1");
        vehicleRepository.saveAll(List.of(moto, car, van));

        addSpot(lotA, new MotorcycleSpot(), 0, true, moto);
        addSpot(lotA, new MotorcycleSpot(), 1, false, null);

        addSpot(lotA, new CarSpot(), 0, true, van);
        addSpot(lotA, new CarSpot(), 1, true, car);

        addSpot(lotA, new LargeSpot(), 0, false, null);

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

        Spot s1 = addSpot(lotA, new CarSpot(), 0, true, car);
        Spot s2 = addSpot(lotA, new LargeSpot(), 0, true, car);
        addSpot(lotA, new CarSpot(), 1, false, null); // libre, ne doit pas apparaître

        em.flush();
        em.clear();

        List<Spot> spots = spotRepository.findAllByVehicle(car);

        assertEquals(2, spots.size());
        assertTrue(spots.stream().anyMatch(s -> s.getId().equals(s1.getId())));
        assertTrue(spots.stream().anyMatch(s -> s.getId().equals(s2.getId())));
    }
}
