package com.nbacon.parkingloot.domain.model.vehicle;

import com.nbacon.parkingloot.domain.exception.VehicleTypeNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTypeTest {

    @Test
    void fromString_validValues_ignoreCase() {
        assertEquals(VehicleType.CAR, VehicleType.fromString("car"));
        assertEquals(VehicleType.MOTORCYCLE, VehicleType.fromString("Motorcycle"));
        assertEquals(VehicleType.VAN, VehicleType.fromString("VAN"));
    }

    @Test
    void fromString_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> VehicleType.fromString(null));
    }

    @Test
    void fromString_invalid_throwsVehicleTypeNotFoundException() {
        VehicleTypeNotFoundException ex = assertThrows(VehicleTypeNotFoundException.class, () -> VehicleType.fromString("plane"));
        assertTrue(ex.getMessage().contains("invalid"));
    }
}
