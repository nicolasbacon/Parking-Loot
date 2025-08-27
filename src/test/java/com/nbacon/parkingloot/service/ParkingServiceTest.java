package com.nbacon.parkingloot.service;

import com.nbacon.parkingloot.domain.exception.NoAvailableSpotException;
import com.nbacon.parkingloot.domain.exception.ParkingNotFoundException;
import com.nbacon.parkingloot.domain.exception.VehicleNotFoundException;
import com.nbacon.parkingloot.domain.factory.VehicleFactory;
import com.nbacon.parkingloot.domain.model.park.ParkingLot;
import com.nbacon.parkingloot.domain.model.park.Spot;
import com.nbacon.parkingloot.domain.model.vehicle.Car;
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
import com.nbacon.parkingloot.service.policy.SpotSelectionPolicy;
import com.nbacon.parkingloot.service.policy.SpotSelectionRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParkingServiceTest {

    ParkingRepository parkingRepo;
    SpotRepository spotRepo;
    SpotSelectionRegistry registry;
    VehicleFactory vehicleFactory;
    VehicleRepository vehicleRepo;

    ParkingService service;

    @BeforeEach
    void setup() {
        parkingRepo = mock(ParkingRepository.class);
        spotRepo = mock(SpotRepository.class);
        registry = mock(SpotSelectionRegistry.class);
        vehicleFactory = new VehicleFactory(); // simple et déterministe
        vehicleRepo = mock(VehicleRepository.class);

        service = new ParkingService(parkingRepo, spotRepo, registry, vehicleFactory, vehicleRepo);
    }

    @Test
    void create_persistsParkingLot() {
        ParkingCreateRequest req = new ParkingCreateRequest(1, 2, 3);
        service.create(req);
        verify(parkingRepo).save(any(ParkingLot.class));
    }

    @Test
    void park_success_assignsAndSaves() {
        long plId = 10L;
        IncomingVehicle in = new IncomingVehicle("car", "AB-123-CD", plId);
        ParkingLot pl = ParkingLot.builder().id(plId).build();
        when(parkingRepo.findById(plId)).thenReturn(Optional.of(pl));

        SpotSelectionPolicy carPolicy = mock(SpotSelectionPolicy.class);
        when(registry.getPolicy(VehicleType.CAR)).thenReturn(carPolicy);
        Spot spot = mock(Spot.class);
        when(carPolicy.selectAllocation(pl)).thenReturn(Optional.of(List.of(spot)));

        service.park(in);

        verify(vehicleRepo).save(any(Vehicle.class));
        verify(spot).assignTo(any(Vehicle.class));
        verify(spotRepo).saveAll(List.of(spot));
    }

    @Test
    void park_whenParkingNotFound_throwsParkingNotFoundException() {
        IncomingVehicle in = new IncomingVehicle("van", "V-1", 999L);
        when(parkingRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ParkingNotFoundException.class, () -> service.park(in));
        verifyNoInteractions(spotRepo, vehicleRepo);
    }

    @Test
    void park_whenNoAllocation_throwsNoAvailableSpotException() {
        long plId = 11L;
        IncomingVehicle in = new IncomingVehicle("motorcycle", "M-1", plId);
        ParkingLot pl = ParkingLot.builder().id(plId).build();
        when(parkingRepo.findById(plId)).thenReturn(Optional.of(pl));

        SpotSelectionPolicy motoPolicy = mock(SpotSelectionPolicy.class);
        when(registry.getPolicy(VehicleType.MOTORCYCLE)).thenReturn(motoPolicy);
        when(motoPolicy.selectAllocation(pl)).thenReturn(Optional.empty());

        assertThrows(NoAvailableSpotException.class, () -> service.park(in));
        verify(vehicleRepo, never()).save(any());
    }

    @Test
    void getAllParkingInformation_success_mapsDto() {
        long plId = 12L;
        ParkingLot pl = ParkingLot.builder().id(plId).build();
        when(parkingRepo.findById(plId)).thenReturn(Optional.of(pl));

        ParkingLotInfos infos = new ParkingLotInfos(3, 10, false, false, List.of("CarSpot"), 2);
        when(spotRepo.fetchParkingLotInfos(plId)).thenReturn(infos);

        ParkingLotInfosResponse resp = service.getAllParkingInformation(plId);
        assertEquals(3, resp.nbSpotRemaining());
        assertEquals(10, resp.totalNbSpot());
        assertFalse(resp.isFull());
        assertFalse(resp.isEmpty());
        assertEquals(List.of("CarSpot"), resp.typesOfSeatsFullyAssigned());
        assertEquals(2, resp.numberOfSpotsVansAssigned());
    }

    @Test
    void getAllParkingInformation_whenParkingNotFound_throws() {
        when(parkingRepo.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ParkingNotFoundException.class, () -> service.getAllParkingInformation(404L));
    }

    @Test
    void leave_success_releasesAllAndDeletesVehicle() {
        OutgoingVehicle out = new OutgoingVehicle("AB-123-CD");
        Vehicle v = new Car("AB-123-CD");
        when(vehicleRepo.findFirstByLicensePlate("AB-123-CD")).thenReturn(v);
        Spot s1 = mock(Spot.class);
        Spot s2 = mock(Spot.class);
        when(spotRepo.findAllByVehicle(v)).thenReturn(List.of(s1, s2));

        service.leave(out);

        verify(s1).release();
        verify(s2).release();
        verify(vehicleRepo).delete(v);
    }

    @Test
    void leave_whenVehicleUnknown_throwsVehicleNotFoundException() {
        OutgoingVehicle out = new OutgoingVehicle("UNKNOWN");
        when(vehicleRepo.findFirstByLicensePlate("UNKNOWN")).thenReturn(null);

        assertThrows(VehicleNotFoundException.class, () -> service.leave(out));
    }
}
