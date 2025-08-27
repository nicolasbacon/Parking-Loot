package com.nbacon.parkingloot.dto.response;

import java.util.List;

public record ParkingLotInfosResponse(
        int nbSpotRemaining,
        int totalNbSpot,
        boolean isFull,
        boolean isEmpty,
        List<String> typesOfSeatsFullyAssigned,
        int numberOfSpotsVansAssigned
) {
}
