package com.nbacon.parkingloot.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OutgoingVehicle(
        @NotBlank String licensePlate
) {
    public OutgoingVehicle {
        licensePlate = licensePlate == null ? null : licensePlate.trim();
    }
}
