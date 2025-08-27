package com.nbacon.parkingloot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbacon.parkingloot.domain.exception.AlreadyFreeSpotException;
import com.nbacon.parkingloot.domain.exception.NoAvailableSpotException;
import com.nbacon.parkingloot.domain.exception.ParkingNotFoundException;
import com.nbacon.parkingloot.domain.exception.VehicleTypeNotFoundException;
import com.nbacon.parkingloot.dto.request.IncomingVehicle;
import com.nbacon.parkingloot.dto.request.OutgoingVehicle;
import com.nbacon.parkingloot.dto.request.ParkingCreateRequest;
import com.nbacon.parkingloot.dto.response.ParkingLotInfosResponse;
import com.nbacon.parkingloot.service.ParkingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ParkingController.class)
class ParkingControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    ParkingService service;

    @Test
    void infos_success() throws Exception {
        Mockito.when(service.getAllParkingInformation(1L)).thenReturn(
                new ParkingLotInfosResponse(3, 10, false, true, List.of("LargeSpot"), 1)
        );

        mvc.perform(get("/parking/infos/{parkingLotId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nbSpotRemaining").value(3))
                .andExpect(jsonPath("$.isEmpty").value(true));
    }

    @Test
    void infos_validation_parkingLotIdMustBePositive() throws Exception {
        mvc.perform(get("/parking/infos/{parkingLotId}", 0))
                .andExpect(status().isBadRequest());
    }

    @Test
    void infos_parkingNotFound_maps404() throws Exception {
        Mockito.when(service.getAllParkingInformation(99L))
                .thenThrow(new ParkingNotFoundException(99L));

        mvc.perform(get("/parking/infos/{parkingLotId}", 99))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No parking available")));
    }

    @Test
    void create_success_returns201() throws Exception {
        ParkingCreateRequest req = new ParkingCreateRequest(1, 2, 3);

        mvc.perform(post("/parking/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void create_validation_negative_notAllowed() throws Exception {
        // champs invalides: constraints @PositiveOrZero
        String invalidJson = """
                {"nbMotorcycleSpot":-1,"nbCarSpot":0,"nbLargeSpot":2}
                """;
        mvc.perform(post("/parking/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("nbMotorcycleSpot"));
    }

    @Test
    void park_success() throws Exception {
        IncomingVehicle req = new IncomingVehicle("car", "AB-123-CD", 1L);

        mvc.perform(post("/parking/park")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void park_validation_notBlank_and_notNull() throws Exception {
        // vehicleType, licensePlate blank; parkingLotId null
        String invalidJson = """
                {"vehicleType":"  ","licensePlate":" ","parkingLotId":null}
                """;
        mvc.perform(post("/parking/park")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void park_vehicleTypeInvalid_maps400() throws Exception {
        Mockito.doThrow(new VehicleTypeNotFoundException("plane"))
                .when(service).park(any(IncomingVehicle.class));

        IncomingVehicle req = new IncomingVehicle("plane", "X", 1L);
        mvc.perform(post("/parking/park")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("invalid")));
    }

    @Test
    void park_noAvailableSpot_maps409() throws Exception {
        Mockito.doThrow(new NoAvailableSpotException("CAR"))
                .when(service).park(any(IncomingVehicle.class));

        IncomingVehicle req = new IncomingVehicle("car", "X", 1L);
        mvc.perform(post("/parking/park")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void leave_success() throws Exception {
        OutgoingVehicle req = new OutgoingVehicle("AB-123-CD");

        mvc.perform(post("/parking/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void leave_validation_licensePlateNotBlank() throws Exception {
        String invalid = """
                {"licensePlate":"  "}
                """;
        mvc.perform(post("/parking/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    void leave_alreadyFree_maps409() throws Exception {
        Mockito.doThrow(new AlreadyFreeSpotException())
                .when(service).leave(any(OutgoingVehicle.class));

        OutgoingVehicle req = new OutgoingVehicle("AB-123-CD");

        mvc.perform(post("/parking/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("already free")));
    }

    @Test
    void park_parkingNotFound_maps404() throws Exception {
        Mockito.doThrow(new ParkingNotFoundException(77L))
                .when(service).park(any(IncomingVehicle.class));

        IncomingVehicle req = new IncomingVehicle("car", "AB-123-CD", 77L);
        mvc.perform(post("/parking/park")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }
}
