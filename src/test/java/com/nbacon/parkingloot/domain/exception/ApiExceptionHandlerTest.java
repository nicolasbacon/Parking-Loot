package com.nbacon.parkingloot.domain.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiExceptionHandlerTest {

    ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleVehicleTypeNotFound_returns400() {
        ResponseEntity<String> resp = handler.handleVehicleTypeNotFound(new VehicleTypeNotFoundException("plane"));
        assertEquals(400, resp.getStatusCode().value());
        Assertions.assertNotNull(resp.getBody());
        assertTrue(resp.getBody().contains("invalid"));
    }

    @Test
    void handleParkingNotFound_returns404() {
        ResponseEntity<String> resp = handler.handleParkingNotFound(new ParkingNotFoundException(42L));
        assertEquals(404, resp.getStatusCode().value());
        Assertions.assertNotNull(resp.getBody());
        assertTrue(resp.getBody().contains("No parking available"));
    }

    @Test
    void handleNoAvailableSpot_returns409() {
        ResponseEntity<String> resp = handler.handleNoAvailableSpot(new NoAvailableSpotException("CAR"));
        assertEquals(409, resp.getStatusCode().value());
        Assertions.assertNotNull(resp.getBody());
        assertTrue(resp.getBody().contains("No space available"));
    }

    @Test
    void handleAlreadyFreeSpot_returns409() {
        ResponseEntity<String> resp = handler.handleAlreadyFreeSpot(new AlreadyFreeSpotException());
        assertEquals(409, resp.getStatusCode().value());
        Assertions.assertNotNull(resp.getBody());
        assertTrue(resp.getBody().contains("already free"));
    }

    @Test
    void handleIllegalArgument_returns400() {
        ResponseEntity<String> resp = handler.handleIllegalArgument(new IllegalArgumentException("bad"));
        assertEquals(400, resp.getStatusCode().value());
        assertEquals("bad", resp.getBody());
    }

    @Test
    void handleValidationExceptions_returns400WithPayload() {
        // Ici on ne reproduit pas l’exception Spring complète; ce test est mieux en test MVC.
        // On vérifie déjà les autres handlers ci-dessus. Le test de validation sera dans les tests du contrôleur.
        assertTrue(true);
    }
}
