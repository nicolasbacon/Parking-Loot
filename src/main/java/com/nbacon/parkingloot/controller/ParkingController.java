package com.nbacon.parkingloot.controller;

import com.nbacon.parkingloot.dto.request.IncomingVehicle;
import com.nbacon.parkingloot.dto.request.ParkingCreateRequest;
import com.nbacon.parkingloot.dto.response.ParkingLotInfosResponse;
import com.nbacon.parkingloot.service.ParkingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> create(@RequestBody @Valid ParkingCreateRequest request) {
        parkingService.create(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/park")
    public void park(@RequestBody @Valid IncomingVehicle vehicle) {
        parkingService.park(vehicle);
    }

}
