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
        

        // String name =
        //     Optional.ofNullable(vehicleElement.getChild("name"))
        //         .orElseThrow(() -> Utils.xmlElementParsingException(
        //             "name", idString, "Expected a child name element, but got nothing"
        //         ))
        //         .getText();
        var nameElement = vehicleElement.getChild("name");
        String name = nameElement == null ? null : nameElement.getText();
        var nameValidationError = Vehicle.validate.name(name);
        if (nameValidationError.isPresent()) {
            throw Utils.xmlElementParsingException("name", idString, nameValidationError.get());
        }


        Element coordinatesElement = vehicleElement.getChild("coordinates");
        Coordinates coordinates = Coordinates.fromXmlElement(coordinatesElement, idString);


        String creationDateString = vehicleElement.getAttributeValue("creation-date");
        LocalDate creationDate = null;
        try {
            creationDate = LocalDate.parse(creationDateString);
        } catch (DateTimeParseException err) {
            throw Utils.xmlAttributeParsingException("creation-date", idString, "date expected, but got '" + creationDateString + "'");
        }


        String enginePowerString = 
            Optional.ofNullable(vehicleElement.getChild("engine-power"))
                .orElseThrow(() -> Utils.xmlElementParsingException(
                    "engine-power", idString, "Expected a child engine-power element, but got nothing"
                ))
                .getText();
        Float enginePower = null;
        try {
            enginePower = Float.parseFloat(enginePowerString);
        } catch (IllegalArgumentException err) {
            throw Utils.xmlAttributeParsingException("engine-power", idString, "number with floating point expected, but got '" + enginePowerString + "'");
        }


        String vehicleTypeString =
            Optional.ofNullable(vehicleElement.getChild("vehicle-type"))
                .orElseThrow(() -> Utils.xmlElementParsingException(
                    "vehicle-type", idString, "Expected a child vehicle-type element, but got nothing"
                ))
                .getText();
        VehicleType vehicleType = null;
        try {
            vehicleType = VehicleType.parse(vehicleTypeString);
        } catch (ParsingException err) {
            throw Utils.xmlElementParsingException("vehicle-type", idString, err);
        }


        String fuelTypeString =
            Optional.ofNullable(vehicleElement.getChild("fuel-type"))
                .orElseThrow(() -> Utils.xmlElementParsingException(
                    "fuel-type", idString, "Expected a child fuel-type element, but got nothing"
                ))
                .getText();
        FuelType fuelType = null;
        try {
            fuelType = FuelType.parse(fuelTypeString);
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
    
    public static Vehicle fromXmlElementa(Element vehicleElement) throws ParsingException {
        
        /* Parsing id */
        String idString = vehicleElement.getAttributeValue("id");
        if (idString == null) {
            throw new ParsingException("'id' attribute: Expected a valid uuid, but got nothing");
        }

        try {
            UUID id = UUID.fromString(idString);    
            
            /* Parsing name */
            String name =
                Optional.ofNullable(vehicleElement.getChild("name"))
                    .orElseThrow(() -> Utils.xmlElementParsingException(
                        "name", idString, "Expected a child name element, but got nothing"
                    ))
                    .getText();
            var nameValidationError = Vehicle.validate.name(name);
            if (nameValidationError.isPresent()) {
                throw Utils.xmlElementParsingException("name", idString, nameValidationError.get());
            }

            /* Parsing coordinates */
            Element coordinatesElement = vehicleElement.getChild("coordinates");
            Coordinates coordinates = Coordinates.fromXmlElement(coordinatesElement, idString);
            
            /* Parsing creation date */
            String creationDateString = vehicleElement.getAttributeValue("creation-date");
            try {
                LocalDate creationDate = LocalDate.parse(creationDateString);

                /* Parsing engine power */
                String enginePowerString = 
                    Optional.ofNullable(vehicleElement.getChild("engine-power"))
                        .orElseThrow(() -> Utils.xmlElementParsingException(
                            "engine-power", idString, "Expected a child engine-power element, but got nothing"
                        ))
                        .getText();
                
                try {
                    Float enginePower = Float.parseFloat(enginePowerString);

                    /* Parsing vehicle type */
                    String vehicleTypeString =
                        Optional.ofNullable(vehicleElement.getChild("vehicle-type"))
                            .orElseThrow(() -> Utils.xmlElementParsingException(
                                "vehicle-type", idString, "Expected a child vehicle-type element, but got nothing"
                            ))
                            .getText();
                    
                    try {
                        VehicleType vehicleType = VehicleType.parse(vehicleTypeString);

                        /* Parsing fuel type */
                        String fuelTypeString =
                            Optional.ofNullable(vehicleElement.getChild("fuel-type"))
                                .orElseThrow(() -> Utils.xmlElementParsingException(
                                    "fuel-type", idString, "Expected a child fuel-type element, but got nothing"
                                ))
                                .getText();
                        
                        try {
                            FuelType fuelType = FuelType.parse(fuelTypeString);
    
                            return new Vehicle(
                                id,
                                name,
                                coordinates,
                                creationDate,
                                enginePower,
                                vehicleType,
                                fuelType
                            );
                        } catch (ParsingException err) {
                            throw Utils.xmlElementParsingException("fuel-type", idString, err);
                        }
                    } catch (ParsingException err) {
                        throw Utils.xmlElementParsingException("vehicle-type", idString, err);
                    }
                } catch (IllegalArgumentException err) {
                    throw Utils.xmlAttributeParsingException("engine-power", idString, "number with floating point expected, but got '" + enginePowerString + "'");
                }
            } catch (DateTimeParseException err) {
                throw Utils.xmlAttributeParsingException("creation-date", idString, "date expected, but got '" + creationDateString + "'");
            }
        } catch (IllegalArgumentException err) {
            throw new ParsingException("'id' attribute: Expected a valid uuid, but got '" + idString + "'");
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
}