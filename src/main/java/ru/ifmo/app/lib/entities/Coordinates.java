package ru.ifmo.app.lib.entities;

import java.util.Optional;


import org.jdom2.Element;

import ru.ifmo.app.lib.VehiclesXmlTag;
import ru.ifmo.app.lib.Utils.NumberParser;
import ru.ifmo.app.lib.Utils.DeprecatedValidator;
import ru.ifmo.app.lib.exceptions.ParsingException;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Record of coordinates stored in the {@link Vehicle} objects
 */
public record Coordinates(
    Long x,
    Integer y
) {
    /**
     * Converts the coordinates to an XML {@link Element}
     */
    public Element toXmlElement() {
        Element coordinatesElement = new Element(VehiclesXmlTag.COORDINATES.toString())
            .setAttribute(VehiclesXmlTag.COORDINATES_X_ATTR.toString(), this.x().toString())
            .setAttribute(VehiclesXmlTag.COORDINATES_Y_ATTR.toString(), this.y().toString());
        return coordinatesElement;
    }

    /**
     * Given the string, parses it with parsingFunction
     * and then validates given number value.
     * If during any of these steps error occures ParsingException is thrown.
     * 
     * @param tagname A tagname which will be specified in the parsing exception message
     * @param vehicleId uuid which will be specified in the parsing exception message
     * 
     * @throws ParsingException Thrown if parsing or validation fails
     */
    private static <N> N parseOneCoordinate(
        String coordinateString,
        NumberParser<N> numberParse,
        DeprecatedValidator<N> validator,
        VehiclesXmlTag tagname,
        String vehicleUUID
    ) throws ParsingException {
        if (coordinateString == null) {
            var validationError = validator.validate(null);
            if (validationError.isPresent()) {
                throw new ParsingException(Messages.get("Error.XmlElement.OfVehicle", VehiclesXmlTag.COORDINATES, vehicleUUID, validationError.get()));
            }
            return null;
        }
        try {
            var parsed = numberParse.parse(coordinateString);
            var validationError = validator.validate(parsed);
            if (validationError.isPresent()) {
                throw new ParsingException(Messages.get("Error.XmlElement.OfVehicle", VehiclesXmlTag.COORDINATES, vehicleUUID, validationError.get()));
            }
            return parsed;
        } catch (NumberFormatException err) {
            throw new ParsingException(
                Messages.get(
                    "Error.XmlElement.OfVehicle",
                    VehiclesXmlTag.COORDINATES,
                    vehicleUUID,
                    Messages.get(
                        "Error.XmlAttribute",
                        tagname,
                        Messages.get("Error.Validation.Required.ButGot", "integer", coordinateString))
                    )
            );
        } 
    }
    
    /**
     * Create a coordinates from the XML {@link Element}.
     * <p>
     * If any of the fields/elements don't pass the validation or parsing
     * processes the parsing of this coordinates object is failed.
     * </p>
     * <p>
     * All of the element/attribute tag names can be found in the {@link VehiclesXmlTag}
     * </p>
     * 
     * @param coordinatesElement the element that is interpreted as coordinates
     * @param vehicleUUID uuid string that is used to produce more detailed error messages
     * 
     * @return Constructed coordinates
     * @throws ParsingException Thrown if parsing or validation fails
     */
    public static Coordinates fromXmlElement(Element coordinatesElement, String vehicleUUID) throws ParsingException {
        if (coordinatesElement == null) {
            return null;
        }

        String xString = coordinatesElement.getAttributeValue(VehiclesXmlTag.COORDINATES_X_ATTR.toString());
        String yString = coordinatesElement.getAttributeValue(VehiclesXmlTag.COORDINATES_Y_ATTR.toString());
        
        Long x = parseOneCoordinate(
            xString,
            Long::parseLong,
            Coordinates.validate::x,
            VehiclesXmlTag.COORDINATES_X_ATTR,
            vehicleUUID
        );

        Integer y = parseOneCoordinate(
            yString,
            Integer::parseInt,
            Coordinates.validate::y,
            VehiclesXmlTag.COORDINATES_Y_ATTR,
            vehicleUUID
        );

        return new Coordinates(x, y);
    }

    /**
     * Staic class, which serves as a namespace for Coordinates fields' validation methods.
     * Every method implements the functional {@link ru.ifmo.app.lib.Utils.DeprecatedValidator Utils.Validator} interface
     */
    public static class validate {
        private validate() {}

        /**
         * Validate the X coordinate
         * 
         * @return Optional with the string, representing an error. If the optional is empty, then the value is valid. Otherwise string should be interpreted as the validation erorr message.
         */
        public static Optional<String> x(Long x) {
            if (x == null) return Optional.of(Messages.get("Error.Validation.Required", Messages.get("Vehicle.Coordinates.X")));
            return Optional.empty();
        }

        /**
         * Validate the Y coordinate
         * 
         * @return Optional with the string, representing an error. If the optional is empty, then the value is valid. Otherwise string should be interpreted as the validation erorr message.
         */
        public static Optional<String> y(Integer y) {
            if (y == null) return Optional.of(Messages.get("Error.Validation.Required", Messages.get("Vehicle.Coordinates.Y")));
            if (y > -738) return Optional.of(Messages.get("Error.Validation.LowerThan", Messages.get("Vehicle.Coordinates.Y"), -737));
            return Optional.empty();
        }
    }
}
