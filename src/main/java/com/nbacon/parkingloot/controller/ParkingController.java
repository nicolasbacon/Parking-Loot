package com.nbacon.parkingloot.controller;

import com.nbacon.parkingloot.dto.request.IncomingVehicle;
import com.nbacon.parkingloot.dto.request.ParkingCreateRequest;
import com.nbacon.parkingloot.model.park.ParkingLot;
import com.nbacon.parkingloot.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
class ParkingController {
    private final ParkingService parkingService;

    @RequestMapping("/")
    public List<ParkingLot> index() {
        return parkingService.getAllParkings();
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
