package com.nbacon.parkingloot.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

public record ParkingCreateRequest(
        @PositiveOrZero int nbMotorcycleSpot,
        @PositiveOrZero int nbCarSpot,
        @PositiveOrZero int nbLargeSpot
) {
}
