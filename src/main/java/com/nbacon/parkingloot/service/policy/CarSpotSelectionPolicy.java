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
public class CarSpotSelectionPolicy implements SpotSelectionPolicy {
    private final SpotRepository spotRepository;


    @Override
    public VehicleType supportedType() {
        return VehicleType.CAR;
    }

    @Override
    public Optional<List<Spot>> selectAllocation(ParkingLot parkingLot) {
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
