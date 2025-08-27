package com.nbacon.parkingloot.repository.dto;

import java.util.List;

public record ParkingLotInfos(
        int nbSpotRemaining,
        int totalNbSpot,
        boolean isFull,
        boolean isEmpty,
        List<String> typesOfSeatsFullyAssigned,
        int numberOfSpotsVansAssigned
) {
}
