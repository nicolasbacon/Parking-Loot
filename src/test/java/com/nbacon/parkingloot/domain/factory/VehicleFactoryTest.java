package com.nbacon.parkingloot.domain.factory;

import com.nbacon.parkingloot.domain.model.vehicle.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleFactoryTest {

    @Test
    void createVehicle_supportedTypes() {
        VehicleFactory f = new VehicleFactory();

        Vehicle car = f.createVehicle(VehicleType.CAR, "C1");
        assertInstanceOf(Car.class, car);
        assertEquals("C1", car.getLicensePlate());

        Vehicle moto = f.createVehicle(VehicleType.MOTORCYCLE, "M1");
        assertInstanceOf(Motorcycle.class, moto);
        assertEquals("M1", moto.getLicensePlate());

        Vehicle van = f.createVehicle(VehicleType.VAN, "V1");
        assertInstanceOf(Van.class, van);
        assertEquals("V1", van.getLicensePlate());
    }

    @Test
    void createVehicle_unsupportedType_throwsIllegalArgumentException() {
        VehicleFactory f = new VehicleFactory();
        assertThrows(IllegalArgumentException.class, () -> f.createVehicle(null, "X"));
    }
}
