package com.nbacon.parkingloot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ServletInitializerTest {

    @Test
    void configure_registersParkingLootApplicationAsSource() {
        ServletInitializer initializer = new ServletInitializer();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();

        SpringApplicationBuilder configured = initializer.configure(builder);
        SpringApplication app = configured.build();

        assertTrue(app.getAllSources().contains(ParkingLootApplication.class),
                "ParkingLootApplication must be registered as a source");
    }


}