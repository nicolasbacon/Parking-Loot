package com.nbacon.parkingloot.domain.model.park;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingLotTest {

    @Test
    void prePersist_addsSpotsPerType_withPositionAndBackReference() {
        ParkingLot pl = ParkingLot.builder()
                .nbMotorcycleSpot(2)
                .nbCarSpot(3)
                .nbLargeSpot(1)
                .build();

        pl.prePersist();

        assertEquals(6, pl.getSpots().size());

        pl.getSpots().forEach(s -> {
            assertNotNull(s.getParkingLot());
            assertEquals(pl, s.getParkingLot());
            assertTrue(s.getPosition() >= 0);
        });

        assertTrue(pl.getSpots().stream().anyMatch(s -> s instanceof MotorcycleSpot));
        assertTrue(pl.getSpots().stream().anyMatch(s -> s instanceof CarSpot));
        assertTrue(pl.getSpots().stream().anyMatch(s -> s instanceof LargeSpot));
    }
}
