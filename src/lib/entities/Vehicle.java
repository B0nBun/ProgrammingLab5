package lib.entities;

import java.time.LocalDate;

public record Vehicle(
    long id,
    String name,
    Coordinates coordinates,
    LocalDate creationDate,
    Float enginePower,
    VehicleType type,
    FuelType fuelType
) implements Comparable<Vehicle> {
    
    @Override
    public String toString() {
        return String.join(
            "\n",
            "Vehicle[",
            "  id = " + this.id + ",",
            "  name = " + this.name + ",",
            "  coordinates = " + this.coordinates.toString() + ",",
            "  creationDate = " + this.creationDate.toString() + ",",
            "  enginePower = " + this.enginePower + ",",
            "  type = " + this.type.name() + ",",
            "  fuelType = " + this.fuelType.name() + ",",
            "]"
        );
    }
    
    @Override
    public int compareTo(Vehicle other) {
        return this.enginePower.compareTo(other.enginePower);
    }
}