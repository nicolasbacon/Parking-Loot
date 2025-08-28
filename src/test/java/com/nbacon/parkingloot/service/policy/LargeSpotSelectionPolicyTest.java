package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.domain.model.park.CarSpot;
import com.nbacon.parkingloot.domain.model.park.LargeSpot;
import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.repository.SpotRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LargeSpotSelectionPolicyTest {

    @Test
    void selectAllocation_largeFirst_elseThreeConsecutiveCarSpots() {
        SpotRepository repo = mock(SpotRepository.class);
        LargeSpotSelectionPolicy policy = new LargeSpotSelectionPolicy(repo);
        UUID parkingLotId = UUID.randomUUID();
        ParkingLot pl = ParkingLot.builder().id(parkingLotId).build();

        Spot largeSpot = new LargeSpot();
        when(repo.findFirstFreeSpotByType(LargeSpot.class, pl))
                .thenReturn(Optional.of(largeSpot));

        Optional<List<Spot>> a1 = policy.selectAllocation(pl);
        assertTrue(a1.isPresent());
        assertEquals(List.of(largeSpot), a1.get());

        when(repo.findFirstFreeSpotByType(LargeSpot.class, pl))
                .thenReturn(Optional.empty());

        Spot c1 = new CarSpot();
        Spot c2 = new CarSpot();
        Spot c3 = new CarSpot();
        when(repo.findThreeConsecutiveFreeSpots(CarSpot.class, pl))
                .thenReturn(List.of(c1, c2, c3));

        Optional<List<Spot>> a2 = policy.selectAllocation(pl);
        assertTrue(a2.isPresent());
        assertEquals(3, a2.get().size());

        when(repo.findThreeConsecutiveFreeSpots(CarSpot.class, pl))
                .thenReturn(List.of(c1, c2)); // pas assez

        Optional<List<Spot>> a3 = policy.selectAllocation(pl);
        assertTrue(a3.isEmpty());
    }
}
