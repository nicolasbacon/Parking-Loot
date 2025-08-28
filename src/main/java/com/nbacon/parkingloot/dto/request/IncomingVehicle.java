package com.nbacon.parkingloot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record IncomingVehicle(
        @NotBlank String vehicleType,
        @NotBlank String licensePlate,
        @NotNull UUID parkingLotId
) {
    public IncomingVehicle {
        vehicleType = vehicleType.trim();
        licensePlate = licensePlate.trim();
    }
}

