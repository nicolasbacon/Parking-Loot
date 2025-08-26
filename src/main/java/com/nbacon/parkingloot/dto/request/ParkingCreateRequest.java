package com.nbacon.parkingloot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ParkingCreateRequest {
        private int nbMotorcycleSpot;
        private int nbCarSpot;
        private int nbLargeSpot;
}
