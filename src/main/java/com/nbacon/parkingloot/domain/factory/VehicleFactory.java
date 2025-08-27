package com.nbacon.parkingloot.domain.factory;

import com.nbacon.parkingloot.domain.model.vehicle.Car;
import com.nbacon.parkingloot.domain.model.vehicle.Motorcycle;
import com.nbacon.parkingloot.domain.model.vehicle.Van;
import com.nbacon.parkingloot.domain.model.vehicle.Vehicle;
import com.nbacon.parkingloot.dto.request.VehicleType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class VehicleFactory {

    private final Map<VehicleType, Function<String, Vehicle>> registry = new EnumMap<>(VehicleType.class);

    public VehicleFactory() {
        this.registry.put(VehicleType.CAR, Car::new);
        this.registry.put(VehicleType.MOTORCYCLE, Motorcycle::new);
        this.registry.put(VehicleType.VAN, Van::new);
    }

    public Vehicle createVehicle(VehicleType type, String licensePlate) {
        Function<String, Vehicle> constructor = registry.get(type);
        if (constructor == null) {
            throw new IllegalArgumentException("Unsupported vehicle type: " + type);
        }
        return constructor.apply(licensePlate);
    }
}
