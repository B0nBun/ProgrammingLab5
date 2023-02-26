package ru.ifmo.app.lib.entities;

import java.util.Optional;

import org.jdom2.Element;

import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.VehiclesXmlTag;
import ru.ifmo.app.lib.exceptions.ParsingException;

public record Coordinates(
    Long x,
    Integer y
) {
    public Element toXmlElement() {
        Element coordinatesElement = new Element(VehiclesXmlTag.Coordinates.toString())
            .setAttribute(VehiclesXmlTag.CoordinatesXAttr.toString(), this.x().toString())
            .setAttribute(VehiclesXmlTag.CoordinatesYAttr.toString(), this.y().toString());
        return coordinatesElement;
    }

    public static Coordinates fromXmlElement(Element coordinatesElement, String vehicleUUID) throws ParsingException {
        if (coordinatesElement == null) {
            return null;
        }

        String xString = coordinatesElement.getAttributeValue(VehiclesXmlTag.CoordinatesXAttr.toString());
        String yString = coordinatesElement.getAttributeValue(VehiclesXmlTag.CoordinatesYAttr.toString());
        
        Long x = null;
        try {
            x = Long.parseLong(xString);
            var xValidationError = Coordinates.validate.x(x);
            if (xValidationError.isPresent()) {
                throw Utils.xmlAttributeParsingException(VehiclesXmlTag.CoordinatesXAttr, vehicleUUID, xValidationError.get());
            }
        } catch (NumberFormatException err) {
            throw Utils.xmlElementParsingException(VehiclesXmlTag.Coordinates, vehicleUUID, "'" + VehiclesXmlTag.CoordinatesXAttr + "' attribute: Long integer required but got '" + xString + "'");
        }

        Integer y = null;
        try {
            y = Integer.parseInt(yString);
            var yValidationError = Coordinates.validate.y(y);
            if (yValidationError.isPresent()) {
                throw Utils.xmlAttributeParsingException(VehiclesXmlTag.CoordinatesYAttr, vehicleUUID, yValidationError.get());
            }
        } catch (NumberFormatException err) {
            throw Utils.xmlElementParsingException(VehiclesXmlTag.Coordinates, vehicleUUID, "'" + VehiclesXmlTag.CoordinatesYAttr + "' attribute: Integer required but got '" + yString + "'");
        }

        return new Coordinates(x, y);
    }
    
    public static class validate {
        private validate() {}

        public static Optional<String> x(Long x) {
            if (x == null) return Optional.of("'x' coordinate can't be empty");
            return Optional.empty();
        }

        public static Optional<String> y(Integer y) {
            if (y == null) return Optional.of("'y' coordinate can't be empty");
            if (y > -738) return Optional.of("'y' coordinate can't be greater than -738");
            return Optional.empty();
        }
    }
}
