package com.nbacon.parkingloot.dto.request;

import com.nbacon.parkingloot.exception.VehicleTypeNotFoundException;

public enum VehicleType {
    CAR("CAR"),
    MOTORCYCLE("MOTORCYCLE"),
    VAN("VAN");

    public final String value;

    VehicleType(String value) {
        this.value = value;
    }

    public static VehicleType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("VehicleType is null");
        }

        for (VehicleType type : VehicleType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new VehicleTypeNotFoundException(value);
    }

}
