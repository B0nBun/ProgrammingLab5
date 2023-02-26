package ru.ifmo.app.lib.entities;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;

import org.jdom2.Element;

import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.exceptions.ParsingException;

public record Vehicle(
    UUID id,
    String name,
    Coordinates coordinates,
    LocalDate creationDate,
    Float enginePower,
    VehicleType type,
    FuelType fuelType
) implements Comparable<Vehicle> {
    
    public static Vehicle fromXmlElement(Element vehicleElement) throws ParsingException {
        String idString = vehicleElement.getAttributeValue("id");
        if (idString == null) {
            throw new ParsingException("'id' attribute: Expected a valid uuid, but got nothing");
        }


        UUID id = null;
        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException err) {
            throw new ParsingException("'id' attribute: Expected a valid uuid, but got '" + idString + "'");
        }
        

        var nameElement = vehicleElement.getChild("name");
        String name = nameElement == null ? null : nameElement.getText();
        var nameValidationError = Vehicle.validate.name(name);
        if (nameValidationError.isPresent()) {
            throw Utils.xmlElementParsingException("name", idString, nameValidationError.get());
        }


        Element coordinatesElement = vehicleElement.getChild("coordinates");
        Coordinates coordinates = Coordinates.fromXmlElement(coordinatesElement, idString);
        var coordinatesValidationError = Vehicle.validate.coordinates(coordinates);
        if (coordinatesValidationError.isPresent()) {
            throw Utils.xmlElementParsingException("coordinates", idString, coordinatesValidationError.get());
        }


        String creationDateString = vehicleElement.getAttributeValue("creation-date");
        LocalDate creationDate = null;
        try {
            if (creationDateString == null) {
                throw Utils.xmlAttributeParsingException("creation-date", idString, "date expected, but got nothing");
            }
            creationDate = LocalDate.parse(creationDateString);
        } catch (DateTimeParseException err) {
            throw Utils.xmlAttributeParsingException("creation-date", idString, "date expected, but got '" + creationDateString + "'");
        }

        
        Element enginePowerElement = vehicleElement.getChild("engine-power");
        String enginePowerString = enginePowerElement == null ? null : enginePowerElement.getText();
        Float enginePower = null;
        try {
            if (enginePowerString == null) {
                var validationError = Vehicle.validate.enginePower(null);
                if (validationError.isPresent()) {
                    throw Utils.xmlElementParsingException("engine-power", idString, validationError.get());
                }
            } else {
                enginePower = Float.parseFloat(enginePowerString);
                var enginePowerValidationError = Vehicle.validate.enginePower(enginePower);
                if (enginePowerValidationError.isPresent()) {
                    throw Utils.xmlElementParsingException("engine-power", idString, enginePowerValidationError.get());
                }
            }
        } catch (IllegalArgumentException err) {
            throw Utils.xmlElementParsingException("engine-power", idString, "number with floating point expected, but got '" + enginePowerString + "'");
        }


        Element vehicleTypeElement = vehicleElement.getChild("vehicle-type");
        String vehicleTypeString = vehicleTypeElement == null ? null : vehicleTypeElement.getText();
        VehicleType vehicleType = null;
        try {
            if (vehicleTypeString == null) {
                var validationError = Vehicle.validate.vehicleType(null);
                if (validationError.isPresent()) {
                    throw Utils.xmlElementParsingException("vehicle-type", idString, validationError.get());
                }
            } else {
                vehicleType = VehicleType.parse(vehicleTypeString);
                var validationError = Vehicle.validate.vehicleType(vehicleType);
                if (validationError.isPresent()) {
                    throw Utils.xmlElementParsingException("vehicle-type", idString, validationError.get());
                }
            }
        } catch (ParsingException err) {
            throw Utils.xmlElementParsingException("vehicle-type", idString, err);
        }


        Element fuelTypeElement = vehicleElement.getChild("fuel-type");
        String fuelTypeString = fuelTypeElement == null ? null : fuelTypeElement.getText();
        FuelType fuelType = null;
        try {
            if (fuelTypeString == null) {
                var validationError = Vehicle.validate.fuelType(null);
                if (validationError.isPresent()) {
                    throw Utils.xmlElementParsingException("fuel-type", idString, validationError.get());
                }
            } else {
                fuelType = FuelType.parse(fuelTypeString);
                var validationError = Vehicle.validate.fuelType(fuelType);
                if (validationError.isPresent()) {
                    throw Utils.xmlElementParsingException("fuel-type", idString, validationError.get());
                }
            }
        } catch (ParsingException err) {
            throw Utils.xmlElementParsingException("fuel-type", idString, err);
        }
        
        return new Vehicle(
            id,
            name,
            coordinates,
            creationDate,
            enginePower,
            vehicleType,
            fuelType
        );
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
            return Optional.empty();
        }
        public static Optional<String> vehicleType(VehicleType type) {
            return Optional.empty();
        }
    }
}