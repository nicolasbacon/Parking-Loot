package com.nbacon.parkingloot;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class ParkingLootApplicationTest {

    @Test
    void main_callsSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(eq(ParkingLootApplication.class), any(String[].class)))
                    .thenReturn(null);

            ParkingLootApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(eq(ParkingLootApplication.class), any(String[].class)));
        }
    }

}