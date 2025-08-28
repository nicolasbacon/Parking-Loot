package com.nbacon.parkingloot.service;

import com.nbacon.parkingloot.domain.exception.AlreadyFreeSpotException;
import com.nbacon.parkingloot.domain.exception.NoAvailableSpotException;
import com.nbacon.parkingloot.domain.exception.ParkingNotFoundException;
import com.nbacon.parkingloot.domain.exception.VehicleNotFoundException;
import com.nbacon.parkingloot.domain.factory.VehicleFactory;
import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.Vehicle;
import com.nbacon.parkingloot.domain.model.vehicle.VehicleType;
import com.nbacon.parkingloot.dto.request.IncomingVehicle;
import com.nbacon.parkingloot.dto.request.OutgoingVehicle;
import com.nbacon.parkingloot.dto.request.ParkingCreateRequest;
import com.nbacon.parkingloot.dto.response.ParkingLotInfosResponse;
import com.nbacon.parkingloot.repository.ParkingRepository;
import com.nbacon.parkingloot.repository.SpotRepository;
import com.nbacon.parkingloot.repository.VehicleRepository;
import com.nbacon.parkingloot.repository.dto.ParkingLotInfos;
import com.nbacon.parkingloot.service.policy.SpotSelectionRegistry;
import jakarta.validation.Valid;
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

    public void create(ParkingCreateRequest request) {
        ParkingLot parkingLot = ParkingLot.builder()
                .nbMotorcycleSpot(request.nbMotorcycleSpot())
                .nbCarSpot(request.nbCarSpot())
                .nbLargeSpot(request.nbLargeSpot())
                .build();
        parkingLotRepository.save(parkingLot);
    }

    @Transactional
    public void park(IncomingVehicle incomingVehicle) {
        VehicleType type = VehicleType.fromString(incomingVehicle.vehicleType());
        Vehicle vehicle = vehicleFactory.createVehicle(type, incomingVehicle.licensePlate());

        ParkingLot parkingLot = parkingLotRepository.findById(incomingVehicle.parkingLotId())
                .orElseThrow(() -> new ParkingNotFoundException(incomingVehicle.parkingLotId()));


        List<Spot> allocation = spotSelectionRegistry.getPolicy(type)
                .selectAllocation(parkingLot)
                .orElseThrow(() -> new NoAvailableSpotException(type.name()));

        vehicleRepository.save(vehicle);

        parkVehicleInSpots(vehicle, allocation);
        spotRepository.saveAll(allocation);
    }

    @Transactional
    public void leave(@Valid OutgoingVehicle outgoingVehicle) {
        Vehicle vehicle = vehicleRepository.findFirstByLicensePlate(outgoingVehicle.licensePlate());
        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle not found");
        }
        List<Spot> spots = spotRepository.findAllByVehicle(vehicle);
        releaseSpots(spots);
        vehicleRepository.delete(vehicle);
    }

    @Transactional(readOnly = true)
    public ParkingLotInfosResponse getAllParkingInformation(long parkingLotId) {
        parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new ParkingNotFoundException(parkingLotId));

        ParkingLotInfos infos = spotRepository.fetchParkingLotInfos(parkingLotId);

        return new ParkingLotInfosResponse(
                infos.nbSpotRemaining(),
                infos.totalNbSpot(),
                infos.isFull(),
                infos.isEmpty(),
                infos.typesOfSeatsFullyAssigned(),
                infos.numberOfSpotsVansAssigned()
        );

    }

    private void parkVehicleInSpots(Vehicle vehicle, List<Spot> spots) {
        boolean allFree = spots.stream().noneMatch(Spot::isOccupied);
        if (!allFree) {
            throw new NoAvailableSpotException(vehicle.getClass().getSimpleName());
        }
        for (Spot spot : spots) {
            spot.setVehicle(vehicle);
            spot.setOccupied(true);
        }
    }

    private void releaseSpots(List<Spot> spots) {
        boolean allTaken = spots.stream().allMatch(Spot::isOccupied);
        if (!allTaken) {
            throw new AlreadyFreeSpotException();
        }
        for (Spot spot : spots) {
            spot.setVehicle(null);
            spot.setOccupied(false);
        }
    }

}
