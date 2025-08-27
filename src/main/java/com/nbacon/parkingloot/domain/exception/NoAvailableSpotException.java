package com.nbacon.parkingloot.domain.exception;

public class NoAvailableSpotException extends RuntimeException {
    public NoAvailableSpotException(String type) {
        super("No space available for a vehicle type : " + type);
    }
}
