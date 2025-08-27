package com.nbacon.parkingloot.domain.model.park;

import com.nbacon.parkingloot.domain.exception.AlreadyFreeSpotException;
import com.nbacon.parkingloot.domain.exception.NoAvailableSpotException;
import com.nbacon.parkingloot.domain.model.vehicle.Car;
import com.nbacon.parkingloot.domain.model.vehicle.Vehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpotTest {
    @Test
    void assignTo_whenFree_setsVehicleAndOccupied() {
        Spot spot = new TestSpot();
        Vehicle v = new Car("AA-123-AA");

        spot.assignTo(v);

        assertTrue(spot.isOccupied());
        assertEquals(v, spot.getVehicle());
    }

    @Test
    void assignTo_whenAlreadyOccupied_throwsNoAvailableSpotException() {
        Spot spot = new TestSpot();
        Vehicle v1 = new Car("AA-123-AA");
        Vehicle v2 = new Car("BB-456-BB");
        spot.assignTo(v1);

        NoAvailableSpotException ex = assertThrows(NoAvailableSpotException.class, () -> spot.assignTo(v2));
        assertTrue(ex.getMessage().contains("Car"));
    }

    @Test
    void release_whenOccupied_clearsVehicleAndOccupied() {
        Spot spot = new TestSpot();
        Vehicle v = new Car("AA-123-AA");
        spot.assignTo(v);

        spot.release();

        assertFalse(spot.isOccupied());
        assertNull(spot.getVehicle());
    }

    @Test
    void release_whenAlreadyFree_throwsAlreadyFreeSpotException() {
        Spot spot = new TestSpot();

        assertThrows(AlreadyFreeSpotException.class, spot::release);
    }

    static class TestSpot extends Spot {
    }


}