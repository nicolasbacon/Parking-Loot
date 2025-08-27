package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.domain.model.park.CarSpot;
import com.nbacon.parkingloot.domain.model.park.LargeSpot;
import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.repository.SpotRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CarSpotSelectionPolicyTest {

    @Test
    void selectAllocation_prefersCar_thenLarge() {
        SpotRepository repo = mock(SpotRepository.class);
        CarSpotSelectionPolicy policy = new CarSpotSelectionPolicy(repo);
        ParkingLot pl = ParkingLot.builder().build();

        Spot carSpot = new CarSpot();
        when(repo.findFirstFreeSpotsByTypeOrderByPosition(CarSpot.class, pl))
                .thenReturn(Optional.of(carSpot));

        Optional<List<Spot>> alloc1 = policy.selectAllocation(pl);
        assertTrue(alloc1.isPresent());
        assertEquals(List.of(carSpot), alloc1.get());

        when(repo.findFirstFreeSpotsByTypeOrderByPosition(CarSpot.class, pl))
                .thenReturn(Optional.empty());
        Spot largeSpot = new LargeSpot();
        when(repo.findFirstFreeSpotsByTypeOrderByPosition(LargeSpot.class, pl))
                .thenReturn(Optional.of(largeSpot));

        Optional<List<Spot>> alloc2 = policy.selectAllocation(pl);
        assertTrue(alloc2.isPresent());
        assertEquals(List.of(largeSpot), alloc2.get());

        when(repo.findFirstFreeSpotsByTypeOrderByPosition(LargeSpot.class, pl))
                .thenReturn(Optional.empty());

        Optional<List<Spot>> alloc3 = policy.selectAllocation(pl);
        assertTrue(alloc3.isEmpty());
    }
}
