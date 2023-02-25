package ru.ifmo.app.lib.entities;

import java.util.Optional;

import org.jdom2.Element;

import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.exceptions.ParsingException;

public record Coordinates(
    Long x,
    Integer y
) {
    public static Coordinates fromXmlElement(Element coordinatesElement, String vehicleUUID) throws ParsingException {
        String xString = coordinatesElement.getAttributeValue("x");
        String yString = coordinatesElement.getAttributeValue("y");
        
        Long x = null;
        try {
            x = Long.parseLong(xString);
        } catch (NumberFormatException err) {
            throw Utils.xmlElementParsingException("coordinates", vehicleUUID, "'x' attribute: Long integer required but got '" + xString + "'");
        }

        Integer y = null;
        try {
            y = Integer.parseInt(yString);
        } catch (NumberFormatException err) {
            throw Utils.xmlElementParsingException("coordinates", vehicleUUID, "'y' attribute: Integer required but got '" + yString + "'");
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
