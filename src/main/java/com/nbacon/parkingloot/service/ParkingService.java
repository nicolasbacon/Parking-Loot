package com.nbacon.parkingloot.service;

import com.nbacon.parkingloot.dto.request.IncomingVehicle;
import com.nbacon.parkingloot.dto.request.ParkingCreateRequest;
import com.nbacon.parkingloot.dto.request.VehicleType;
import com.nbacon.parkingloot.exception.NoAvailableSpotException;
import com.nbacon.parkingloot.exception.ParkingNotFoundException;
import com.nbacon.parkingloot.model.park.ParkingLot;
import com.nbacon.parkingloot.model.park.Spot;
import com.nbacon.parkingloot.model.vehicle.Car;
import com.nbacon.parkingloot.model.vehicle.Motorcycle;
import com.nbacon.parkingloot.model.vehicle.Van;
import com.nbacon.parkingloot.model.vehicle.Vehicle;
import com.nbacon.parkingloot.repository.*;
import com.nbacon.parkingloot.service.policy.SpotAllocation;
import com.nbacon.parkingloot.service.policy.SpotSelectionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingService {
    private final ParkingRepository parkingLotRepository;
    private final SpotRepository spotRepository;
    private final SpotSelectionRegistry spotSelectionRegistry;

    public List<ParkingLot> getAllParkings() {
        return parkingLotRepository.findAll();
    }

    public ParkingLot create(ParkingCreateRequest request) {
        ParkingLot parkingLot = ParkingLot.builder()
                .nbMotorcycleSpot(request.getNbMotorcycleSpot())
                .nbCarSpot(request.getNbCarSpot())
                .nbLargeSpot(request.getNbLargeSpot())
                .build();
        return parkingLotRepository.save(parkingLot);
    }

    @Transactional
    public void park(IncomingVehicle incomingVehicle) {
        VehicleType type = VehicleType.fromString(incomingVehicle.getVehicleType());
        Vehicle vehicle = switch (type) {
            case CAR -> new Car(incomingVehicle.getLicensePlate());
            case MOTORCYCLE -> new Motorcycle(incomingVehicle.getLicensePlate());
            case VAN -> new Van(incomingVehicle.getLicensePlate());
        };

        ParkingLot parkingLot = parkingLotRepository.findById(incomingVehicle.getParkingLotId())
                .orElseThrow(() -> new ParkingNotFoundException(incomingVehicle.getParkingLotId()));


        SpotAllocation allocation = spotSelectionRegistry.getPolicy(type)
                .selectAllocation(parkingLot)
                .orElseThrow(() -> new NoAvailableSpotException(type.value));

        for (Spot spot : allocation.spots()) {
            spot.park(vehicle);
        }
        spotRepository.saveAll(allocation.spots());
    }
}
