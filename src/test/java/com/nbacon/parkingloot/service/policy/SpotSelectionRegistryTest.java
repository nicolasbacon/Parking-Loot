package com.nbacon.parkingloot.service.policy;

import com.nbacon.parkingloot.domain.model.vehicle.VehicleType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpotSelectionRegistryTest {

    @Test
    void getPolicy_returnsRegisteredPolicy() {
        SpotSelectionPolicy p1 = mock(SpotSelectionPolicy.class);
        when(p1.supportedType()).thenReturn(VehicleType.CAR);

        SpotSelectionPolicy p2 = mock(SpotSelectionPolicy.class);
        when(p2.supportedType()).thenReturn(VehicleType.MOTORCYCLE);

        SpotSelectionRegistry reg = new SpotSelectionRegistry(List.of(p1, p2));

        assertEquals(p1, reg.getPolicy(VehicleType.CAR));
        assertEquals(p2, reg.getPolicy(VehicleType.MOTORCYCLE));
    }

    @Test
    void getPolicy_missingType_throwsIllegalArgumentException() {
        SpotSelectionRegistry reg = new SpotSelectionRegistry(List.of());
        assertThrows(IllegalArgumentException.class, () -> reg.getPolicy(VehicleType.VAN));
    }
}
