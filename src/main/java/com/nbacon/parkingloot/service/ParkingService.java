package com.nbacon.parkingloot.service;

import com.nbacon.parkingloot.domain.exception.NoAvailableSpotException;
import com.nbacon.parkingloot.domain.exception.ParkingNotFoundException;
import com.nbacon.parkingloot.domain.factory.VehicleFactory;
import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.Vehicle;
import com.nbacon.parkingloot.dto.request.IncomingVehicle;
import com.nbacon.parkingloot.dto.request.ParkingCreateRequest;
import com.nbacon.parkingloot.dto.request.VehicleType;
import com.nbacon.parkingloot.repository.ParkingRepository;
import com.nbacon.parkingloot.repository.SpotRepository;
import com.nbacon.parkingloot.repository.VehicleRepository;
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
    private final VehicleFactory vehicleFactory;
    private final VehicleRepository vehicleRepository;

    public List<ParkingLot> getAllParkings() {
        return parkingLotRepository.findAll();
    }

    public ParkingLot create(ParkingCreateRequest request) {
        ParkingLot parkingLot = ParkingLot.builder()
                .nbMotorcycleSpot(request.nbMotorcycleSpot())
                .nbCarSpot(request.nbCarSpot())
                .nbLargeSpot(request.nbLargeSpot())
                .build();
        return parkingLotRepository.save(parkingLot);
    }

    @Transactional
    public void park(IncomingVehicle incomingVehicle) {
        VehicleType type = VehicleType.fromString(incomingVehicle.vehicleType());
        Vehicle vehicle = vehicleFactory.createVehicle(type, incomingVehicle.licensePlate());

        ParkingLot parkingLot = parkingLotRepository.findById(incomingVehicle.parkingLotId())
                .orElseThrow(() -> new ParkingNotFoundException(incomingVehicle.parkingLotId()));


        List<Spot> allocation = spotSelectionRegistry.getPolicy(type)
                .selectAllocation(parkingLot)
                .orElseThrow(() -> new NoAvailableSpotException(type.value));

        vehicleRepository.save(vehicle);

        for (Spot spot : allocation) {
            spot.park(vehicle);
        }
        spotRepository.saveAll(allocation);
    }
}
