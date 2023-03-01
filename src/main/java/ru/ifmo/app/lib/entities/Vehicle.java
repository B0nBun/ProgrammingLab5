package ru.ifmo.app.lib.entities;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jdom2.Element;
import ru.ifmo.app.lib.VehiclesXmlTag;
import ru.ifmo.app.lib.Utils.ParsingFunction;
import ru.ifmo.app.lib.Utils.Validator;
import ru.ifmo.app.lib.exceptions.ParsingException;
import ru.ifmo.app.lib.utils.Messages;

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
    
    /**
     * Extracts a text content from the given element, parses gotten string with parsingFunction
     * and then validates given value. If during any of these steps error occures ParsingException is thrown.
     * 
     * @param tagname A tagname which will be specified in the parsing exception message
     * @param vehicleId uuid which will be specified in the parsing exception message
     * 
     * @throws ParsingException Thrown if parsing or validation fails
     */
    private static <T> T parseAndValidateElementContent(
        Element element,
        VehiclesXmlTag tagname,
        Validator<T> validator,
        ParsingFunction<T> parsingFunction,
        UUID vehicleId
    ) throws ParsingException {
        if (element == null) return null;
        String textContent = element.getText();
        if (textContent == null) {
            var validationError = validator.validate(null);
            if (validationError.isPresent()) {
                throw new ParsingException(Messages.get("Error.XmlElement.ofVehicle", tagname, vehicleId, validationError.get()));
            }
            return null;
        }
        try {
            T parsed = parsingFunction.tryToParse(textContent);
            var validationError = validator.validate(parsed);
            if (validationError.isPresent()) {
                throw new ParsingException(validationError.get());
            }
            return parsed;
        } catch (ParsingException err) {
            throw new ParsingException(Messages.get("Error.XmlElement.ofVehicle", tagname, vehicleId, err.getMessage()));
        }
    }

    /**
     * Constructs a new Vehicle object derived from provided Xml.
     * <p>
     * If any of the fields/elements don't pass the validation or parsing
     * processes the parsing of this vehicle is failed.
     * </p>
     * <p>
     * All of the element/attribute tag names can be found in the {@link VehiclesXmlTag}
     * </p>
     * 
     * @param vehicleElement The element which should be interpreted as a vehicle
     * @return Constructed Vehicle object
     * 
     * @throws ParsingException Thrown if any of the data doesn't pass the parsing or the validation processes
     */
    public static Vehicle fromXmlElement(Element vehicleElement) throws ParsingException {
        String idString = vehicleElement.getAttributeValue(VehiclesXmlTag.IdAttr.toString());
        if (idString == null) {
            throw new ParsingException(Messages.get("Error.XmlAttribute.RequiredButGot", VehiclesXmlTag.IdAttr, "uuid", "nothing"));
        }

        UUID id = null;
        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException err) {
            throw new ParsingException(Messages.get("Error.XmlAttribute.RequiredButGot", VehiclesXmlTag.IdAttr, "uuid", idString));
        }

        var nameElement = vehicleElement.getChild(VehiclesXmlTag.Name.toString());
        String name = nameElement == null ? null : nameElement.getText();
        var nameValidationError = Vehicle.validate.name(name);
        if (nameValidationError.isPresent()) {
            throw new ParsingException(Messages.get("Error.XmlElement.OfVehicle", VehiclesXmlTag.Name, id, nameValidationError.get()));
        }

        Element coordinatesElement = vehicleElement.getChild(VehiclesXmlTag.Coordinates.toString());
        Coordinates coordinates = Coordinates.fromXmlElement(coordinatesElement, idString);
        var coordinatesValidationError = Vehicle.validate.coordinates(coordinates);
        if (coordinatesValidationError.isPresent()) {
            throw new ParsingException(Messages.get("Error.XmlElement.OfVehicle", VehiclesXmlTag.Coordinates, id, coordinatesValidationError.get()));
        }

        String creationDateString = vehicleElement.getAttributeValue(VehiclesXmlTag.CreationDateAttr.toString());
        LocalDate creationDate = null;
        try {
            if (creationDateString == null) {
                throw new ParsingException(Messages.get("Error.XmlAttribute.OfVehicle", VehiclesXmlTag.CreationDateAttr, idString, Messages.get("Error.Validation.Required.ButGot", "date", "nothing")));
            }
            creationDate = LocalDate.parse(creationDateString);
        } catch (DateTimeParseException err) {
            throw new ParsingException(Messages.get("Error.XmlAttribute.OfVehicle", VehiclesXmlTag.CreationDateAttr, id, Messages.get("Error.Validation.Required.ButGot", "date", creationDateString)));
        }
       
        Element enginePowerElement = vehicleElement.getChild(VehiclesXmlTag.EnginePower.toString());
        Float enginePower = parseAndValidateElementContent(
            enginePowerElement,
            VehiclesXmlTag.EnginePower,
            Vehicle.validate::enginePower,
            str -> {
                try {
                    return Float.parseFloat(str);
                } catch (NumberFormatException err) {
                    throw new ParsingException(err.getMessage());
                }
            },
            id
        );

        Element vehicleTypeElement = vehicleElement.getChild(VehiclesXmlTag.VehicleType.toString());
        VehicleType vehicleType = parseAndValidateElementContent(
            vehicleTypeElement,
            VehiclesXmlTag.VehicleType,
            Vehicle.validate::vehicleType,
            VehicleType::parse, 
            id
        );

        Element fuelTypeElement = vehicleElement.getChild(VehiclesXmlTag.FuelType.toString());
        FuelType fuelType = parseAndValidateElementContent(
            fuelTypeElement,
            VehiclesXmlTag.FuelType,
            Vehicle.validate::fuelType,
            FuelType::parse,
            id
        );
        
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
    
    /**
     * Compares the vehicles by enginePower field
     */
    @Override
    public int compareTo(Vehicle other) {
        return this.enginePower.compareTo(other.enginePower);
    }

    /**
     * Staic class, which serves as a namespace for Vehicle fields' validation methods.
     * Every method implements the functional {@link ru.ifmo.app.lib.Utils.Validator Utils.Validator} interface
     */
    public static class validate {
        private validate() {}
        
        /**
         * Validate the vehicle name
         * 
         * @return Optional with the string, representing an error. If the optional is empty, then the value is valid. Otherwise string should be interpreted as the validation erorr message.
         */
        public static Optional<String> name(String name) {
            if (name == null || name.length() == 0)
                return Optional.of(Messages.get("Error.Validation.Required", Messages.get("Vehicle.Name")));
            return Optional.empty();
        }

        /**
         * Validate the vehicle coordinates (Coordinates also have their own validation methods, which are not checked here)
         * 
         * @return Optional with the string, representing an error. If the optional is empty, then the value is valid. Otherwise string should be interpreted as the validation erorr message.
         */
        public static Optional<String> coordinates(Coordinates coordinates) {
            if (coordinates == null)
                return Optional.of(Messages.get("Error.Validation.Required", Messages.get("Vehicle.Coordinates")));
            return Optional.empty();
        }
        
        /**
         * Validate the vehicle engine power
         * 
         * @return Optional with the string, representing an error. If the optional is empty, then the value is valid. Otherwise string should be interpreted as the validation erorr message.
         */
        public static Optional<String> enginePower(Float enginePower) {
            if (enginePower == null) return Optional.of(Messages.get("Error.Validation.Required", Messages.get("Vehicle.EnginePower")));
            if (enginePower <= 0) return Optional.of(Messages.get("Error.Validation.GreaterThan", Messages.get("Vehicle.EnginePower")));
            return Optional.empty();
        }
        
        /**
         * Validate the vehicle fuel type
         * 
         * @return Optional with the string, representing an error. If the optional is empty, then the value is valid. Otherwise string should be interpreted as the validation erorr message.
         */
        public static Optional<String> fuelType(FuelType type) {
            return Optional.empty();
        }
        
        /**
         * Validate the vehicle vehicle type
         * 
         * @return Optional with the string, representing an error. If the optional is empty, then the value is valid. Otherwise string should be interpreted as the validation erorr message.
         */
        public static Optional<String> vehicleType(VehicleType type) {
            return Optional.empty();
        }
    }}
