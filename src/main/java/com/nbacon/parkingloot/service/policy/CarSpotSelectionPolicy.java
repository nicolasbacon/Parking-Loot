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
public class CarSpotSelectionPolicy implements SpotSelectionPolicy {
    private final CarSpotRepository carSpotRepository;
    private final LargeSpotRepository largeSpotRepository;


    @Override
    public VehicleType supportedType() {
        return VehicleType.CAR;
    }

    @Override
    public Optional<SpotAllocation> selectAllocation(ParkingLot parkingLot) {
        Optional<Spot> carSpot = carSpotRepository.findByOccupiedFalseAndParkingLot(parkingLot)
                .stream().findFirst().map(Spot.class::cast);
        if (carSpot.isPresent()) {
            return Optional.of(new SpotAllocation(List.of(carSpot.get())));
        }
        Optional<Spot> largeSpot = largeSpotRepository.findByOccupiedFalseAndParkingLot(parkingLot)
                .stream().findFirst().map(Spot.class::cast);
        return largeSpot.map(spot -> new SpotAllocation(List.of(spot)));
    }
}
