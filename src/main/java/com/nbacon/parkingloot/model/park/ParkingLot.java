package com.nbacon.parkingloot.model.park;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLot {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Spot> spots = new ArrayList<>();
    private int nbMotorcycleSpot;
    private int nbCarSpot;
    private int nbLargeSpot;

    @PrePersist
    public void prePersist() {
        addSpots(MotorcycleSpot::new, nbMotorcycleSpot);
        addSpots(CarSpot::new, nbCarSpot);
        addSpots(LargeSpot::new, nbLargeSpot);
    }

    private void addSpots(Supplier<? extends Spot> constructeur, int nombre) {
        for (int i = 0; i < nombre; i++) {
            Spot spot = constructeur.get();
            spot.setParkingLot(this);
            this.spots.add(spot);

        }
    }
}
