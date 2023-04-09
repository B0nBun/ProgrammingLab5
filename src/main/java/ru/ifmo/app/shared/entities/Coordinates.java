package ru.ifmo.app.shared.entities;

import org.jdom2.Element;

import ru.ifmo.app.local.lib.exceptions.ParsingException;
import ru.ifmo.app.local.lib.exceptions.ValidationException;
import ru.ifmo.app.shared.VehiclesXmlTag;
import ru.ifmo.app.shared.utils.fieldschema.FieldSchema;
import ru.ifmo.app.shared.utils.fieldschema.FieldSchemaNum;

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
     * Function that gets x and y coordinates from the passed Xml element.
     * If the `coordinatesElement` argument is null, then returns null,
     * otherwise it parses the attributes and validates them, returning 
     * the newly constructed Coordinates object.
     * 
     * @param coordinatesElement Xml element from which the attributes should be taken
     * @return A newly constructed and validated Coordinates
     * @throws ParsingException Thrown when function can't parse any of the coordinates
     * @throws ValidationException Thrown if coordinates are invalid, according to fields in {@link Coordinates.fields}
     */
    public static Coordinates fromXmlElement(Element coordinatesElement) throws ParsingException, ValidationException {
        if (coordinatesElement == null) return null;
        String coordinateTag = VehiclesXmlTag.COORDINATES.toString();

        Long x = null;
        try {
            String xString = coordinatesElement.getAttributeValue(VehiclesXmlTag.COORDINATES_X_ATTR.toString());
            x = Coordinates.fields.x.fromString(xString);
        } catch (ValidationException err) {
            throw new ValidationException(coordinateTag + " " + VehiclesXmlTag.COORDINATES_X_ATTR.toString() + ": " + err.getMessage());
        } catch (ParsingException err) {
            throw new ParsingException(coordinateTag + " " + VehiclesXmlTag.COORDINATES_X_ATTR.toString() + ": " + err.getMessage());
        }

        Integer y = null;
        try {
            String yString = coordinatesElement.getAttributeValue(VehiclesXmlTag.COORDINATES_Y_ATTR.toString());
            y = Coordinates.fields.y.fromString(yString);
        } catch (ValidationException err) {
            throw new ValidationException(coordinateTag + " " + VehiclesXmlTag.COORDINATES_Y_ATTR.toString() + ": " + err.getMessage());
        } catch (ParsingException err) {
            throw new ParsingException(coordinateTag + " " + VehiclesXmlTag.COORDINATES_Y_ATTR.toString() + ": " + err.getMessage());
        }

        return new Coordinates(x, y);
    }

    /**
     * Static class, which serves as a namespace for {@link FieldSchema FieldSchemas} representing the fields stored in the Coordinates class.
     */
    public static final class fields {
        public static final FieldSchemaNum<Long> x = FieldSchema.longint().nonnull();
        public static final FieldSchemaNum<Integer> y = FieldSchema.integer().nonnull().max(-738);
    }
}
