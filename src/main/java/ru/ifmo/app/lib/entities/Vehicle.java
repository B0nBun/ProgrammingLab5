package ru.ifmo.app.lib.entities;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jdom2.Element;

import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.VehiclesXmlTag;
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
    
    public Element toXmlElement() {
        Element vehicleElement = new Element(VehiclesXmlTag.Vehicle.toString())
            .setAttribute(VehiclesXmlTag.IdAttr.toString(), this.id.toString())
            .setAttribute(VehiclesXmlTag.CreationDateAttr.toString(), this.creationDate.toString())
            .addContent(List.of(
                new Element(VehiclesXmlTag.Name.toString()).setText(this.name()),
                this.coordinates().toXmlElement(),
                new Element(VehiclesXmlTag.EnginePower.toString()).setText(this.enginePower().toString()),
                new Element(VehiclesXmlTag.VehicleType.toString()).setText(this.type().toString()),
                new Element(VehiclesXmlTag.FuelType.toString()).setText(this.fuelType().toString())
            ));

        return vehicleElement;
    }
    
    public static Vehicle fromXmlElement(Element vehicleElement) throws ParsingException {
        String idString = vehicleElement.getAttributeValue(VehiclesXmlTag.IdAttr.toString());
        if (idString == null) {
            throw new ParsingException("'" + VehiclesXmlTag.IdAttr + "' attribute: Expected a valid uuid, but got nothing");
        }


        UUID id = null;
        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException err) {
            throw new ParsingException("'" + VehiclesXmlTag.IdAttr + "' attribute: Expected a valid uuid, but got '" + idString + "'");
        }
        

        var nameElement = vehicleElement.getChild(VehiclesXmlTag.Name.toString());
        String name = nameElement == null ? null : nameElement.getText();
        var nameValidationError = Vehicle.validate.name(name);
        if (nameValidationError.isPresent()) {
            throw Utils.xmlElementParsingException(VehiclesXmlTag.Name, idString, nameValidationError.get());
        }


        Element coordinatesElement = vehicleElement.getChild(VehiclesXmlTag.Coordinates.toString());
        Coordinates coordinates = Coordinates.fromXmlElement(coordinatesElement, idString);
        var coordinatesValidationError = Vehicle.validate.coordinates(coordinates);
        if (coordinatesValidationError.isPresent()) {
            throw Utils.xmlElementParsingException(VehiclesXmlTag.Coordinates, idString, coordinatesValidationError.get());
        }


        String creationDateString = vehicleElement.getAttributeValue(VehiclesXmlTag.CreationDateAttr.toString());
        LocalDate creationDate = null;
        try {
            if (creationDateString == null) {
                throw Utils.xmlAttributeParsingException(VehiclesXmlTag.CreationDateAttr, idString, "date expected, but got nothing");
            }
            creationDate = LocalDate.parse(creationDateString);
        } catch (DateTimeParseException err) {
            throw Utils.xmlAttributeParsingException(VehiclesXmlTag.CreationDateAttr, idString, "date expected, but got '" + creationDateString + "'");
        }

        
        Element enginePowerElement = vehicleElement.getChild(VehiclesXmlTag.EnginePower.toString());
        String enginePowerString = enginePowerElement == null ? null : enginePowerElement.getText();
        Float enginePower = null;
        try {
            if (enginePowerString == null) {
                var validationError = Vehicle.validate.enginePower(null);
                if (validationError.isPresent()) {
                    throw Utils.xmlElementParsingException(VehiclesXmlTag.EnginePower, idString, validationError.get());
                }
            } else {
                enginePower = Float.parseFloat(enginePowerString);
                var enginePowerValidationError = Vehicle.validate.enginePower(enginePower);
                if (enginePowerValidationError.isPresent()) {
                    throw Utils.xmlElementParsingException(VehiclesXmlTag.EnginePower, idString, enginePowerValidationError.get());
                }
            }
        } catch (IllegalArgumentException err) {
            throw Utils.xmlElementParsingException(VehiclesXmlTag.EnginePower, idString, "number with floating point expected, but got '" + enginePowerString + "'");
        }


        Element vehicleTypeElement = vehicleElement.getChild(VehiclesXmlTag.VehicleType.toString());
        String vehicleTypeString = vehicleTypeElement == null ? null : vehicleTypeElement.getText();
        VehicleType vehicleType = null;
        try {
            if (vehicleTypeString == null) {
                var validationError = Vehicle.validate.vehicleType(null);
                if (validationError.isPresent()) {
                    throw Utils.xmlElementParsingException(VehiclesXmlTag.VehicleType, idString, validationError.get());
                }
            } else {
                vehicleType = VehicleType.parse(vehicleTypeString);
                var validationError = Vehicle.validate.vehicleType(vehicleType);
                if (validationError.isPresent()) {
                    throw Utils.xmlElementParsingException(VehiclesXmlTag.VehicleType, idString, validationError.get());
                }
            }
        } catch (ParsingException err) {
            throw Utils.xmlElementParsingException(VehiclesXmlTag.VehicleType, idString, err);
        }


        Element fuelTypeElement = vehicleElement.getChild(VehiclesXmlTag.FuelType.toString());
        String fuelTypeString = fuelTypeElement == null ? null : fuelTypeElement.getText();
        FuelType fuelType = null;
        try {
            if (fuelTypeString == null) {
                var validationError = Vehicle.validate.fuelType(null);
                if (validationError.isPresent()) {
                    throw Utils.xmlElementParsingException(VehiclesXmlTag.FuelType, idString, validationError.get());
                }
            } else {
                fuelType = FuelType.parse(fuelTypeString);
                var validationError = Vehicle.validate.fuelType(fuelType);
                if (validationError.isPresent()) {
                    throw Utils.xmlElementParsingException(VehiclesXmlTag.FuelType, idString, validationError.get());
                }
            }
        } catch (ParsingException err) {
            throw Utils.xmlElementParsingException(VehiclesXmlTag.FuelType, idString, err);
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