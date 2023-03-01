package ru.ifmo.app.lib.entities;

import java.util.Optional;

import org.jdom2.Element;

import ru.ifmo.app.lib.VehiclesXmlTag;
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
        Element coordinatesElement = new Element(VehiclesXmlTag.Coordinates.toString())
            .setAttribute(VehiclesXmlTag.CoordinatesXAttr.toString(), this.x().toString())
            .setAttribute(VehiclesXmlTag.CoordinatesYAttr.toString(), this.y().toString());
        return coordinatesElement;
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

        String xString = coordinatesElement.getAttributeValue(VehiclesXmlTag.CoordinatesXAttr.toString());
        String yString = coordinatesElement.getAttributeValue(VehiclesXmlTag.CoordinatesYAttr.toString());
        
        Long x = null;
        try {
            x = Long.parseLong(xString);
            var xValidationError = Coordinates.validate.x(x);
            if (xValidationError.isPresent()) {
                throw new ParsingException(Messages.get("Error.XmlElement.OfVehicle", VehiclesXmlTag.Coordinates, vehicleUUID, xValidationError.get()));
            }
        } catch (NumberFormatException err) {
            throw new ParsingException(
                Messages.get(
                    "Error.XmlElement.OfVehicle",
                    VehiclesXmlTag.Coordinates,
                    vehicleUUID,
                    Messages.get(
                        "Error.XmlAttribute",
                        VehiclesXmlTag.CoordinatesXAttr,
                        Messages.get("Error.Validation.Required.ButGot", "integer", xString))
                    )
            );
        }

        Integer y = null;
        try {
            y = Integer.parseInt(yString);
            var yValidationError = Coordinates.validate.y(y);
            if (yValidationError.isPresent()) {
                throw new ParsingException(Messages.get("Error.XmlElement.OfVehicle", VehiclesXmlTag.Coordinates, vehicleUUID, yValidationError.get()));
            }
        } catch (NumberFormatException err) {
            throw new ParsingException(
                Messages.get(
                    "Error.XmlElement.OfVehicle",
                    VehiclesXmlTag.Coordinates,
                    vehicleUUID,
                    Messages.get(
                        "Error.XmlAttribute",
                        VehiclesXmlTag.CoordinatesYAttr,
                        Messages.get("Error.Validation.Required.ButGot", "integer", yString)
                    )
                )
            );
        }

        return new Coordinates(x, y);
    }

    /**
     * Staic class, which serves as a namespace for Coordinates fields' validation methods.
     * Every method implements the functional {@link ru.ifmo.app.lib.Utils#Validator Utils.Validator} interface
     */
    public static class validate {
        private validate() {}

        /**
         * Validate the X coordinate
         * 
         * @return Optional with the string, representing an error. If the optional is empty, then the value is valid. Otherwise string should be interpreted as the validation erorr message.
         */
        public static Optional<String> x(Long x) {
            if (x == null) return Optional.of(Messages.get("Error.Validation.Required", Messages.get("Vehicle.Coordinate.X")));
            return Optional.empty();
        }

        /**
         * Validate the Y coordinate
         * 
         * @return Optional with the string, representing an error. If the optional is empty, then the value is valid. Otherwise string should be interpreted as the validation erorr message.
         */
        public static Optional<String> y(Integer y) {
            if (y == null) return Optional.of(Messages.get("Error.Validation.Required", Messages.get("Vehicle.Coordinate.Y")));
            if (y > -738) return Optional.of(Messages.get("Error.Validation.LowerThan", Messages.get("Vehicle.Coordinate.Y"), -737));
            return Optional.empty();
        }
    }
}
