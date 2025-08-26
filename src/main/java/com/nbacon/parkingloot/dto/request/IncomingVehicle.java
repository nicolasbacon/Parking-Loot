package com.nbacon.parkingloot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IncomingVehicle {
    private String vehicleType;
    private String licensePlate;
    private Long parkingLotId;
}
