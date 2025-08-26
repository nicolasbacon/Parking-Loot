package com.nbacon.parkingloot.model.vehicle;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Van extends Vehicle {
    public Van(String licensePlate) {
        super(licensePlate);
    }
}
