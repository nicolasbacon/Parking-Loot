package com.nbacon.parkingloot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IncomingVehicle(
        @NotBlank String vehicleType,
        @NotBlank String licensePlate,
        @NotNull Long parkingLotId
) {
    public IncomingVehicle {
        vehicleType = vehicleType.trim();
        licensePlate = licensePlate.trim();
    }
}

