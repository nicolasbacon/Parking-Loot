package com.nbacon.parkingloot.domain.exception;

import java.util.UUID;

public class ParkingNotFoundException extends RuntimeException {
    public ParkingNotFoundException(UUID parkingLotId) {
        super("No parking available for the ID : " + parkingLotId);
    }
}
