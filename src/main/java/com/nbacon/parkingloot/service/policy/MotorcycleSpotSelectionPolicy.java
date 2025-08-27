package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.domain.model.park.*;
import com.nbacon.parkingloot.dto.request.VehicleType;
import com.nbacon.parkingloot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MotorcycleSpotSelectionPolicy implements SpotSelectionPolicy {
    private final SpotRepository spotRepository;

    @Override
    public VehicleType supportedType() {
        return VehicleType.MOTORCYCLE;
    }

    @Override
    public Optional<List<Spot>> selectAllocation(ParkingLot parkingLot) {
        Optional<Spot> motocycleSpot = spotRepository.findFreeSpotsByTypeOrderByPosition(MotorcycleSpot.class, parkingLot)
                .stream()
                .findFirst();
        if (motocycleSpot.isPresent()) {
            return Optional.of(List.of(motocycleSpot.get()));
        }
        Optional<Spot> carSpot = spotRepository.findFreeSpotsByTypeOrderByPosition(CarSpot.class, parkingLot)
                .stream().findFirst();
        if (carSpot.isPresent()) {
            return Optional.of(List.of(carSpot.get()));
        }
        Optional<Spot> largeSpot = spotRepository.findFreeSpotsByTypeOrderByPosition(LargeSpot.class, parkingLot)
                .stream().findFirst();
        return largeSpot.map(List::of);
    }
}
