package lib.entities;

import lib.exceptions.InvalidArgumentException;

public record Vehicle(
    long id,
    String name,
    Coordinates coordinates,
    java.time.LocalDate creationDate,
    Float enginePower,
    VehicleType type,
    FuelType fuelType
) implements Comparable<Vehicle> {
    
    public static Vehicle constructChecked(
        long id,
        String name,
        Coordinates coordinates,
        java.time.LocalDate creationDate,
        Float enginePower,
        VehicleType type, 
        FuelType fuelType 
    ) throws InvalidArgumentException {
        
        if (id <= 0)
            throw new InvalidArgumentException("id", "id must be greater than 0");

        if (name == null || name.length() == 0)
            throw new InvalidArgumentException("name", "name is required and can't be empty");

        if (coordinates == null)
            throw new InvalidArgumentException("coordinates", "coordinates are required");

        if (creationDate == null)
            throw new InvalidArgumentException("creationDate", "creationDate is required");

        if (enginePower == null || enginePower <= 0)
            throw new InvalidArgumentException("enginePower", "enginePower is required");

        if (type == null)
            throw new InvalidArgumentException("type", "type is required");

        if (fuelType == null)
            throw new InvalidArgumentException("fuelType", "fuelType is required");

        return new Vehicle(id, name, coordinates, creationDate, enginePower, type, fuelType);
    }

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