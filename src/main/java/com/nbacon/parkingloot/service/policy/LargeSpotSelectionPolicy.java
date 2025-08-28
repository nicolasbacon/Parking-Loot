package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.domain.model.park.CarSpot;
import com.nbacon.parkingloot.domain.model.park.LargeSpot;
import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.VehicleType;
import com.nbacon.parkingloot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LargeSpotSelectionPolicy implements SpotSelectionPolicy {
    private final SpotRepository spotRepository;

    @Override
    public VehicleType supportedType() {
        return VehicleType.VAN;
    }

    @Override
    public Optional<List<Spot>> selectAllocation(ParkingLot parkingLot) {
        Optional<Spot> large = spotRepository.findFirstFreeSpotByType(LargeSpot.class, parkingLot);
        if (large.isPresent()) {
            return Optional.of(List.of(large.get()));
        }

        List<Spot> freeCarSpots = spotRepository.findThreeConsecutiveFreeSpots(CarSpot.class, parkingLot);
        if (freeCarSpots.size() == 3) {
            return Optional.of(freeCarSpots);
        }
        return Optional.empty();

    }

}
