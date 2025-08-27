package com.nbacon.parkingloot.domain.model.park;

import com.nbacon.parkingloot.domain.exception.AlreadyFreeSpotException;
import com.nbacon.parkingloot.domain.exception.NoAvailableSpotException;
import com.nbacon.parkingloot.domain.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "SPOT_TYPE")
@Table(name = "SPOT", indexes = {
        @Index(name = "idx_spot_type_occupied_parking_position",
                columnList = "SPOT_TYPE, OCCUPIED, PARKING_LOT_ID, POSITION")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_spot_parking_type_position",
                columnNames = {"SPOT_TYPE", "PARKING_LOT_ID", "POSITION"})
})
@Getter
@Setter
public abstract class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean occupied;

    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARKING_LOT_ID")
    private ParkingLot parkingLot;

    public void park(Vehicle vehicle) {
        if (this.occupied) {
            throw new NoAvailableSpotException(vehicle.getClass().getSimpleName());
        }
        this.vehicle = vehicle;
        this.occupied = true;
    }

    public void leave() {
        if (!this.occupied) {
            throw new AlreadyFreeSpotException();
        }
        this.vehicle = null;
        this.occupied = false;
    }

}
