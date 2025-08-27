package com.nbacon.parkingloot.domain.model.vehicle;

import com.nbacon.parkingloot.domain.exception.VehicleTypeNotFoundException;

public enum VehicleType {
    CAR,
    MOTORCYCLE,
    VAN;

    public static VehicleType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("VehicleType is null");
        }
        try {
            return VehicleType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new VehicleTypeNotFoundException(value);
        }
    }
}

