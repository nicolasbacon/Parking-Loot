package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.domain.model.park.CarSpot;
import com.nbacon.parkingloot.domain.model.park.LargeSpot;
import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.VehicleType;
import com.nbacon.parkingloot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
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
        Optional<Spot> large = spotRepository.findFreeSpotsByTypeOrderByPosition(LargeSpot.class, parkingLot)
                .stream().findFirst();
        if (large.isPresent()) {
            return Optional.of(List.of(large.get()));
        }

        List<Spot> freeCarSpots = find3ConsecutiveCarSpots(parkingLot);

        if (freeCarSpots.size() == 3) {
            return Optional.of(freeCarSpots);
        }
        return Optional.empty();
    }

    private List<Spot> find3ConsecutiveCarSpots(ParkingLot parkingLot) {
        List<Spot> availableSpots = spotRepository.findFreeSpotsByTypeOrderByPosition(CarSpot.class, parkingLot);

        for (int i = 0; i <= availableSpots.size() - 3; i++) {
            Spot spot1 = availableSpots.get(i);
            Spot spot2 = availableSpots.get(i + 1);
            Spot spot3 = availableSpots.get(i + 2);

            if (spot2.getPosition() == spot1.getPosition() + 1 &&
                    spot3.getPosition() == spot2.getPosition() + 1) {

                return Arrays.asList(spot1, spot2, spot3);
            }
        }

        return Collections.emptyList();
    }

}
