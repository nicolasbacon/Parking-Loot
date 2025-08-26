package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.dto.request.VehicleType;
import com.nbacon.parkingloot.model.park.ParkingLot;
import com.nbacon.parkingloot.model.park.Spot;
import com.nbacon.parkingloot.repository.CarSpotRepository;
import com.nbacon.parkingloot.repository.LargeSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LargeSpotSelectionPolicy implements SpotSelectionPolicy {
    private final LargeSpotRepository largeSpotRepository;
    private final CarSpotRepository carSpotRepository;

    @Override
    public VehicleType supportedType() {
        return VehicleType.VAN;
    }

    @Override
    public Optional<SpotAllocation> selectAllocation(ParkingLot parkingLot) {
        Optional<Spot> large = largeSpotRepository.findByOccupiedFalseAndParkingLot(parkingLot)
                .stream().findFirst().map(Spot.class::cast);
        if (large.isPresent()) {
            return Optional.of(new SpotAllocation(List.of(large.get())));
        }

        List<Spot> freeCars = carSpotRepository.findByOccupiedFalseAndParkingLot(parkingLot)
                .stream().limit(3).map(Spot.class::cast).toList();

        if (freeCars.size() == 3) {
            return Optional.of(new SpotAllocation(freeCars));
        }
        return Optional.empty();
    }

}
