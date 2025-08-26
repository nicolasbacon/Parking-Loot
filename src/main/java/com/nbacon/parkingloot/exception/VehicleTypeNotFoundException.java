package com.nbacon.parkingloot.exception;

public class VehicleTypeNotFoundException extends RuntimeException {
    public VehicleTypeNotFoundException(String type) {
        super("Vehicle type is invalid : " + type);
    }
}
