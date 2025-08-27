package com.nbacon.parkingloot.domain.exception;

public class VehicleTypeNotFoundException extends RuntimeException {
    public VehicleTypeNotFoundException(String type) {
        super("Vehicle type is invalid : " + type);
    }
}
