package com.nbacon.parkingloot.controller;

import com.nbacon.parkingloot.model.vehicle.Car;
import com.nbacon.parkingloot.model.vehicle.Vehicle;
import com.nbacon.parkingloot.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
class VehicleController {
    private final VehicleService vehicleService;

    @RequestMapping("/")
    public List<Vehicle> index() {
        return vehicleService.getAll();
    }

    @PostMapping("/create")
    public Vehicle create() {
        Vehicle vehicle = new Car();
        return vehicleService.saveVehicle(vehicle);
    }

}
