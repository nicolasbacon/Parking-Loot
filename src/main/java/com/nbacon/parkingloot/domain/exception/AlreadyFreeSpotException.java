package com.nbacon.parkingloot.domain.exception;

public class AlreadyFreeSpotException extends RuntimeException {
    public AlreadyFreeSpotException() {
        super("Spot is already free");
    }
}
