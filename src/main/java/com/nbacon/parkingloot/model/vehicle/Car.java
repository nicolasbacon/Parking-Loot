package com.nbacon.parkingloot.model.vehicle;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate);
    }
}