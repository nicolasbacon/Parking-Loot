package com.nbacon.parkingloot.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VehicleTypeNotFoundException.class)
    public ResponseEntity<String> handleVehicleTypeNotFound(VehicleTypeNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ParkingNotFoundException.class)
    public ResponseEntity<String> handleParkingNotFound(ParkingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NoAvailableSpotException.class)
    public ResponseEntity<String> handleNoAvailableSpot(NoAvailableSpotException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}

