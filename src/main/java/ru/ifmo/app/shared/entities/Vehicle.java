package ru.ifmo.app.shared.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.jdom2.Element;
import ru.ifmo.app.local.lib.exceptions.ParsingException;
import ru.ifmo.app.local.lib.exceptions.ValidationException;
import ru.ifmo.app.shared.VehiclesXmlTag;
import ru.ifmo.app.shared.utils.Messages;
import ru.ifmo.app.shared.utils.fieldschema.FieldSchema;
import ru.ifmo.app.shared.utils.fieldschema.FieldSchemaEnum;
import ru.ifmo.app.shared.utils.fieldschema.FieldSchemaNum;
import ru.ifmo.app.shared.utils.fieldschema.FieldSchemaString;

/**
 * The element which is stored in the collection.
 */
public record Vehicle(
    UUID id,
    String name,
    Coordinates coordinates,
    LocalDate creationDate,
    Float enginePower,
    VehicleType type,
    FuelType fuelType
) implements Comparable<Vehicle> {
    
    /**
     * Creates an XML {@link Element} of the Vehicle
     * 
     * @return The XML element with all of the data stored as it's children
     */
    public Element toXmlElement() {
        Element vehicleElement = new Element(VehiclesXmlTag.VEHICLE.toString())
            .setAttribute(VehiclesXmlTag.ID_ATTR.toString(), this.id.toString())
            .setAttribute(VehiclesXmlTag.CREATION_DATE_ATTR.toString(), this.creationDate.toString())
            .addContent(List.of(
                new Element(VehiclesXmlTag.NAME.toString()).setText(this.name()),
                this.coordinates().toXmlElement(),
                new Element(VehiclesXmlTag.ENGINE_POWER.toString()).setText(this.enginePower().toString()),
                new Element(VehiclesXmlTag.VEHICLE_TYPE.toString()).setText(this.type().toString()),
                new Element(VehiclesXmlTag.FUEL_TYPE.toString()).setText(this.fuelType().toString())
            ));

        return vehicleElement;
    }

    private static <T> T getFieldFromVehicleXmlElement(Element vehicleElement, String fieldTagName, FieldSchema<T, ?> fieldSchema) throws ParsingException, ValidationException {
        try {
            Element neededElement = vehicleElement.getChild(fieldTagName);
            String elementContent = null;
            if (neededElement != null) {
                elementContent = neededElement.getText();
            }
            return fieldSchema.fromString(elementContent);
        } catch (ValidationException err) {
            throw new ValidationException(fieldTagName + ": " + err.getMessage());
        } catch (ParsingException err) {
            throw new ParsingException(fieldTagName + ": " + err.getMessage());
        }
    }
    
    /**
     * Constructs a new Vehicle object derived from provided Xml.
     * <p>
     * If any of the fields/elements don't pass the validation or parsing,
     * appropriate exception is thrown
     * </p>
     * <p>
     * All of the element/attribute tag names can be found in the {@link VehiclesXmlTag}
     * </p>
     * 
     * @param vehicleElement The element which should be interpreted as a vehicle
     * @return Constructed Vehicle object
     * 
     * @throws ParsingException Thrown if any of the data doesn't pass the parsing processes
     * @throws ValidationException Thrown if any of the data doesn't pass the validation processes of {@link Vehicle.fields}
     */
    public static Vehicle fromXmlElement(Element vehicleElement) throws ParsingException, ValidationException {        
        String idString = vehicleElement.getAttributeValue(VehiclesXmlTag.ID_ATTR.toString());
        if (idString == null) {
            throw new ParsingException(Messages.get("Error.XmlAttribute.RequiredButGot", VehiclesXmlTag.ID_ATTR, "uuid", "nothing"));
        }
        try {
            UUID id = null;
            try {
                id = UUID.fromString(idString);
            } catch (IllegalArgumentException err) {
                throw new ParsingException(Messages.get("Error.XmlAttribute.RequiredButGot", VehiclesXmlTag.ID_ATTR, "uuid", idString));
            }
    
            String name = Vehicle.getFieldFromVehicleXmlElement(vehicleElement, VehiclesXmlTag.NAME.toString(), Vehicle.fields.name);
            
            Element coordinatesElement = vehicleElement.getChild(VehiclesXmlTag.COORDINATES.toString());
            Coordinates coordinates = Coordinates.fromXmlElement(coordinatesElement);
            if (coordinates == null) {
                throw new ParsingException(Messages.get("Error.XmlElement.RequiredByGot", VehiclesXmlTag.COORDINATES, "uuid", "nothing"));
            }
    
            String creationDateString = vehicleElement.getAttributeValue(VehiclesXmlTag.CREATION_DATE_ATTR.toString());
            if (creationDateString == null) {
                throw new ValidationException(Messages.get("Error.XmlAttribute.RequiredButGot", VehiclesXmlTag.CREATION_DATE_ATTR, "date", "nothing"));
            }
            LocalDate creationDate = FieldSchema.localdate().parse(creationDateString);
    
            Float enginePower = Vehicle.getFieldFromVehicleXmlElement(vehicleElement, VehiclesXmlTag.ENGINE_POWER.toString(), Vehicle.fields.enginePower);
            VehicleType vehicleType = Vehicle.getFieldFromVehicleXmlElement(vehicleElement, VehiclesXmlTag.VEHICLE_TYPE.toString(), Vehicle.fields.vehicleType);
            FuelType fuelType = Vehicle.getFieldFromVehicleXmlElement(vehicleElement, VehiclesXmlTag.FUEL_TYPE.toString(), Vehicle.fields.fuelType);
    
            return new Vehicle(
                id,
                name,
                coordinates,
                creationDate,
                enginePower,
                vehicleType,
                fuelType
            );
        } catch (ValidationException err) {
            throw new ValidationException("Vehicle with id='" + idString + "': " + err.getMessage());
        } catch (ParsingException err) {
            throw new ParsingException("Vehicle with id='" + idString + "': " + err.getMessage());
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
    
    /**
     * Compares the vehicles by enginePower field
     */
    @Override
    public int compareTo(Vehicle other) {
        return this.name.compareTo(other.name);
    }

    /**
     * Static class, which serves as a namespace for {@link FieldSchema FieldSchemas} representing the fields stored in the Vehicle class.
     */
    public static final class fields {
        public static final FieldSchemaString name = FieldSchema.str().nonnull().nonempty();
        public static final FieldSchemaNum<Float> enginePower = FieldSchema.floating().nonnull().greaterThan(0f);
        public static final FieldSchemaEnum<FuelType> fuelType = FieldSchema.enumeration(FuelType.class);
        public static final FieldSchemaEnum<VehicleType> vehicleType = FieldSchema.enumeration(VehicleType.class);
    }}
