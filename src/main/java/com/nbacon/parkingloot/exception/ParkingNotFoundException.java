package com.nbacon.parkingloot.exception;

public class ParkingNotFoundException extends RuntimeException {
    public ParkingNotFoundException(Long parkingLotId) {
        super("No parking available for the ID : " + parkingLotId);
    }
}
