package com.nbacon.parkingloot.controller;

import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.dto.request.IncomingVehicle;
import com.nbacon.parkingloot.dto.request.ParkingCreateRequest;
import com.nbacon.parkingloot.dto.response.ParkingLotInfosResponse;
import com.nbacon.parkingloot.service.ParkingService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
class ParkingController {
    private final ParkingService parkingService;

    @RequestMapping("/infos/{parkingLotId}")
    public ParkingLotInfosResponse infos(
            @PathVariable
            @Positive(message = "parkingLotId must be strictly positive")
            long parkingLotId
    ) {
        return parkingService.getAllParkingInformation(parkingLotId);
    }

    @PostMapping("/create")
    public ParkingLot create(@RequestBody ParkingCreateRequest request) {
        return parkingService.create(request);
    }

    @PostMapping("/park")
    public void park(@RequestBody IncomingVehicle vehicle) {
        parkingService.park(vehicle);
    }

}
