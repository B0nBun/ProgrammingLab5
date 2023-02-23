package ru.ifmo.app.lib.entities;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record Vehicle(
    UUID id,
    String name,
    Coordinates coordinates,
    LocalDate creationDate,
    Float enginePower,
    VehicleType type,
    FuelType fuelType
) implements Comparable<Vehicle> {
    
    public static class validate {
        private validate() {}
        
        public static Optional<String> name(String name) {
            if (name == null || name.length() == 0)
                return Optional.of("'name' can't be empty");
            return Optional.empty();
        }
        public static Optional<String> coordinates(Coordinates coordinates) {
            if (coordinates == null)
                return Optional.of("'coordinates' can't be empty");
            return Optional.empty();
        }
        public static Optional<String> enginePower(Float enginePower) {
            if (enginePower == null) return Optional.of("'enginePower' can't be empty");
            if (enginePower <= 0) return Optional.of("'enginePower' must be greater than 0");
            return Optional.empty();
        }
        public static Optional<String> fuelType(FuelType type) {
            if (type == null)
                return Optional.of("'fuelType' can't be empty");
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return String.join(
            "\n",
            "Vehicle[",
            "  id = " + this.id + ",",
            "  name = " + this.name + ",",
            "  coordinates = " + this.coordinates + ",",
            "  creationDate = " + this.creationDate + ",",
            "  enginePower = " + this.enginePower + ",",
            "  type = " + this.type + ",",
            "  fuelType = " + this.fuelType + ",",
            "]"
        );
    }
    
    @Override
    public int compareTo(Vehicle other) {
        return this.enginePower.compareTo(other.enginePower);
    }
}