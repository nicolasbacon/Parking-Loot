package com.nbacon.parkingloot.exception;

public class AlreadyFreeSpotException extends RuntimeException {
    public AlreadyFreeSpotException() {
        super("Spot is already free");
    }
}
