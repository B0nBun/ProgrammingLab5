package ru.ifmo.app.shared;

import com.fasterxml.uuid.Generators;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.xml.XMLConstants;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import ru.ifmo.app.local.App;
import ru.ifmo.app.local.lib.exceptions.ParsingException;
import ru.ifmo.app.local.lib.exceptions.ValidationException;
import ru.ifmo.app.shared.entities.Coordinates;
import ru.ifmo.app.shared.entities.FuelType;
import ru.ifmo.app.shared.entities.Vehicle;
import ru.ifmo.app.shared.entities.VehicleType;
import ru.ifmo.app.shared.utils.Messages;
import ru.ifmo.app.shared.utils.Peekable;

/**
 * The class responsible for storage of the collection of {@link Vehicle} objects during the runtime
 * of the program.
 * <p>
 * Implements constrained, but flexible methods through which the collection can be interfaced with
 * (getting, removing, mutating elements).
 * </p>
 * <p>
 * Uses {@link Deque} to store the elements
 * </p>
 */
public class Vehicles {

    /**
     * Creation date, that can be specified either during construction or set to LocalDate.now()
     * during construction
     */
    private final LocalDate creationDate;

    /**
     * A Peekable iterator, which generates a sequence of ids, which are set to the added vehicles
     */
    private final Peekable<UUID> idGenerator;

    /**
     * A Deque collection, which holds all of the vehicle elements
     */
    private Deque<Vehicle> collection;

    /**
     * Class constructor with specified initial collection and creationDate
     *
     * @param vehicles A collection of vehicles, which will be used to create an initial Deque
     */
    public Vehicles(Collection<Vehicle> vehicles, LocalDate creationDate) {
        this.creationDate = creationDate;
        var uuidGenerator = Generators.randomBasedGenerator();
        this.idGenerator =
            new Peekable<>(
                Stream
                    .iterate(uuidGenerator.generate(), __ -> uuidGenerator.generate())
                    .iterator()
            );
        this.collection = new ArrayDeque<>(vehicles);
    }

    /**
     * Class cosntructor which sets the collection to an empty Deque and a creationDate to
     * LocalDate.now()
     */
    public Vehicles() {
        this(new ArrayList<>(), LocalDate.now());
    }

    /**
     * Creates an XML {@link Element} with the current collection
     *
     * @return The XML element with all of the currently stored {@link Vehicle} Objects as it's
     *         children
     */
    public Element toXmlElement() {
        var rootVehicles = new Element(VehiclesXmlTag.VEHICLES.toString())
            .setAttribute(
                VehiclesXmlTag.CREATION_DATE_ATTR.toString(),
                this.creationDate.toString()
            );

        List<Element> vehicleElements = this.stream().map(Vehicle::toXmlElement).toList();
        rootVehicles.addContent(vehicleElements);

        return rootVehicles;
    }

    /**
     * Provided an xml element representing the Vehicles collection, get and parse the creationDate
     * attribute (the tag can be found in {@link VehiclesXmlTag}). If the attribute is absent or can't
     * be parsed, warning/error is logged and {@code null} is returned.
     *
     * @param vehiclesElement Xml {@link Element} from which attribute is extracted
     * @return local date extracted from the element (null if it couldn't be done)
     */
    private static LocalDate getCreationDateFromVehiclesElement(Element vehiclesElement) {
        String creationDateString = vehiclesElement.getAttributeValue(
            VehiclesXmlTag.CREATION_DATE_ATTR.toString()
        );
        if (creationDateString == null) {
            App.logger.warn(
                Messages.get(
                    "Warn.CreationDateNotFound",
                    VehiclesXmlTag.CREATION_DATE_ATTR
                )
            );
            return null;
        }

        try {
            var creationDate = LocalDate.parse(creationDateString);
            return creationDate;
        } catch (DateTimeParseException err) {
            App.logger.error(
                Messages.get("Error.Parsing.CreationDate", err.getMessage())
            );
            return null;
        }
    }

    /**
     * Provided a list of Xml {@link Element elements}, traverses and extracts the Vehicle data from
     * each of the elements, after which a list of constructed {@link Vehicle vehicles} is returned
     * <p>
     * If any element doesn't pass either the data parsing or the validation, the error is logged and
     * the vehicle is skipped, not getting included in the list.
     * </p>
     *
     * @param vehicleElements A list of Xml {@link Element elements} from which vehicles are to be
     *        derived
     * @return A list of {@link Vehicle} objects
     */
    private static List<Vehicle> getVehicleListFromElements(
        List<Element> vehicleElements
    ) {
        var vehicles = new ArrayList<Vehicle>();

        for (var vehicleElement : vehicleElements) {
            try {
                Vehicle vehicle = Vehicle.fromXmlElement(vehicleElement);
                vehicles.add(vehicle);
            } catch (ParsingException | ValidationException err) {
                App.logger.error(
                    Messages.get("Error.Parsing.CollectionElement", err.getMessage())
                );
            }
        }

        return vehicles;
    }

    /**
     * Constructs a new Vehicles object derived from provided Xml.
     * <p>
     * If during construction all of the Xml is valid, but some vehicle doesn't pass a validation or
     * contains an invalid type of data, it is skipped and the parsing continues to process the rest
     * of the elements.
     * </p>
     * <p>
     * If the {@code vehicles} root element doesn't have a {@code creation-date} attribute specified,
     * then it is set to the current LocalDate.now().
     * </p>
     * <p>
     * All of the element/attribute tag names can be found in the {@link VehiclesXmlTag}
     * </p>
     *
     * @param xmlInputStream
     * @return a Vehicles object, which contains all of the valid vehicles from the provided Xml
     *
     * @throws IOException Exception that can be thrown from the SaxBuilder::build method
     * @throws JDOMException Exception that can be thrown from the SaxBuilder::build method
     *
     * @see SAXBuilder
     */
    public static Vehicles loadFromXml(InputStream xmlInputStream)
        throws IOException, JDOMException {
        var sax = new SAXBuilder();

        // https://rules.sonarsource.com/java/RSPEC-2755
        // prevent xxe
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        Document doc = sax.build(xmlInputStream);

        Element rootElement = doc.getRootElement();
        LocalDate creationDate = getCreationDateFromVehiclesElement(rootElement);

        if (creationDate == null) {
            creationDate = LocalDate.now();
            App.logger.warn(Messages.get("Warn.UsingCurrentDay", creationDate));
        }

        List<Element> vehicleElements = rootElement.getChildren();
        List<Vehicle> vehicles = getVehicleListFromElements(vehicleElements);

        return new Vehicles(vehicles, creationDate);
    }

    /**
     * Method used to "read" the currently stored Vehicle
     *
     * @return A newly constructed stream of the collection
     */
    public Stream<Vehicle> stream() {
        return this.collection.stream();
    }

    /**
     * Method used to deterministically know which id will be generated if the new Vehicle is added
     * <p>
     * This was implemented because the {@link ru.ifmo.app.local.lib.commands.AddIfMaxCommand}), which
     * can itself depend on the 'id' field, which is why it is neccesary that a command can know what
     * Vehicle will be generated next.
     * </p>
     *
     * @return The 'id' that will be assigned to the next vehicle added to the collection
     */
    public UUID peekNextId() {
        return this.idGenerator.peek();
    }

    /**
     * Method used to deterministically know which creationDate will be generated if the new Vehicle
     * is added
     * <p>
     * This was implemented because the {@link ru.ifmo.app.local.lib.commands.AddIfMaxCommand}), which
     * can itself depend on the 'creationDate' field, which is why it is neccesary that a command can
     * know what Vehicle will be generated next.
     * </p>
     *
     * @return The 'creationDate' that will be assigned to the next vehicle added to the collection,
     *         if it will happen immediately (since the program can't yet see in the future)
     */
    public LocalDate peekNextCreationDate() {
        return LocalDate.now();
    }

    /**
     * Add the vehicle with the fields form provided schema to the collection. Fields omitted from
     * {@link VehicleCreationSchema} are automatically generated
     *
     * @param newVehicle A creation schema with fields that specify data, which aren't supposed to be
     *        genereated automatically
     */
    public Vehicle add(VehicleCreationSchema newVehicle) {
        Vehicle vehicle = newVehicle.generate(
            this.idGenerator.next(),
            this.peekNextCreationDate()
        );
        this.collection.add(vehicle);
        return vehicle;
    }

    /**
     * Mutate each element in the collection (basically the same as Stream.map but it isn't pure and
     * has to return the same type).
     * <p>
     * The method traverses through the current collection and calls a provided callback for each of
     * the elements, adding a returned, mutated element to the newly constructed Deque, which is then
     * assigned as the new collection.
     * </p>
     *
     * @param mutator Function which accepts a Vehicle from the current collection and returns a new
     *        (maybe mutated or maybe the same) vehicle. Callback mutator shouldn't return
     *        {@code null}, since it isn't checked in any way.
     *
     * @return {@code this} object, returned for method chaining
     */
    public Vehicles mutate(Function<Vehicle, Vehicle> mutator) {
        var newDeque = new ArrayDeque<Vehicle>(this.collection.size());
        for (var vehicle : this.collection) {
            var mutated = mutator.apply(vehicle);
            newDeque.add(mutated);
        }
        this.collection = newDeque;
        return this;
    }

    /**
     * Traverse through each element in the collection and remove it if the predicate result is true
     * (basically the same as a Stream.filter, but isn't pure and instead changes the internal
     * collection)
     *
     * @param predicate A callback which determines what kinds of elements are removed. if a predicate
     *        returns {@code false} in response to the element, then this element is deleted.
     * @return boolean, which is set to {@code true} if any element was removed
     */
    public boolean removeIf(Predicate<Vehicle> predicate) {
        return this.collection.removeIf(predicate);
    }

    /**
     * Clear the collection, leaving it empty of any elements
     *
     * @return {@code this} object, returned for method chaining
     */
    public Vehicles clear() {
        this.collection.clear();
        return this;
    }

    public Vehicles replaceCollectionWith(Vehicles vehicles) {
        this.collection.clear();
        vehicles
            .stream()
            .forEach(v -> {
                this.collection.add(v);
            });
        return this;
    }

    /**
     * @return The name of the class of internal collection
     */
    public String collectionType() {
        return this.collection.getClass().getName();
    }

    /**
     * @return The creation date of the collection
     */
    public LocalDate creationDate() {
        return this.creationDate;
    }

    /**
     * Record which stores all {@link Vehicle} fields, which
     * are not generated automatically.
     * <p>
     * It's main purpose is to seperate the automatically generated data from the user-specified one,
     * which is why it implements the methods responsible for generating a {@link Vehicle} from user input.
     * </p>
     */
    public static record VehicleCreationSchema(
        String name,
        Coordinates coordinates,
        Float enginePower,
        VehicleType type,
        FuelType fuelType
    )
        implements Serializable {
        /**
         * Constructor used to create a VehicleCreationSchema from already existing {@link Vehicle} object
         * @param vehicle Vehicle, values of which are copied to the constructed VehicleCreationSchema
         */
        public VehicleCreationSchema(Vehicle vehicle) {
            this(
                vehicle.name(),
                vehicle.coordinates(),
                vehicle.enginePower(),
                vehicle.type(),
                vehicle.fuelType()
            );
        }

        /**
         * Constructs a {@link Vehicle} object with internal and provided through arguments values.
         *
         * @param id
         * @param creationDate
         *
         * @return A newly constructed Vehicle with all of the specified fields
         */
        public Vehicle generate(UUID id, LocalDate creationDate) {
            return new Vehicle(
                id,
                this.name,
                this.coordinates,
                creationDate,
                this.enginePower,
                this.type,
                this.fuelType
            );
        }

        /**
         * Create a VehicleCreationSchema from an input gotten from provided scanner.
         * <p>
         * Each value is gotten with fields from {@link Vehicle.fields} or {@link Coordinates.fields ru.ifmo.app.lib.entities.Coordinates.fields}
         * via {@link FieldSchema#promptUntilValid ru.ifmo.app.lib.utils.fieldschema.FieldSchema#promptUntilValid(String, Scanner, boolean)} method
         * </p>
         *
         * @param scanner A Scanner which will be used to get the input line by line
         * @param example VehicleCreationSchema fields of which will be logged as an example for each prompt
         *                (e.g. "name (some-vehicle-name):"). If it is set to {@code null} then the example
         *                and the parenthesis are not logged (e.g. "name:").
         *
         * @return A {@code VehicleCreationSchema} created from the input from the scanner with already validated data
         */
        public static VehicleCreationSchema createFromScanner(
            Scanner scanner,
            VehicleCreationSchema example,
            boolean logScanned
        ) {
            BiFunction<String, Function<VehicleCreationSchema, Object>, String> withExample = (
                promptString,
                exampleField
            ) -> {
                if (example == null) {
                    return promptString;
                }
                return promptString + " (" + exampleField.apply(example) + ")";
            };

            String name = Vehicle.fields.name.promptUntilValid(
                withExample.apply(Messages.get("Vehicle.Name"), v -> v.name()),
                scanner,
                "unreachable",
                logScanned
            );
            Long coordinateX = Coordinates.fields.x.promptUntilValid(
                withExample.apply(
                    Messages.get("Vehicle.Coordinates.X"),
                    v -> v.coordinates().x()
                ),
                scanner,
                Messages.get("Error.Validation.Required", "long integer"),
                logScanned
            );
            Integer coordinateY = Coordinates.fields.y.promptUntilValid(
                withExample.apply(
                    Messages.get("Vehicle.Coordinates.Y"),
                    v -> v.coordinates().y()
                ),
                scanner,
                Messages.get("Error.Validation.Required", "integer"),
                logScanned
            );
            Float enginePower = Vehicle.fields.enginePower.promptUntilValid(
                withExample.apply(
                    Messages.get("Vehicle.EnginePower"),
                    v -> v.enginePower()
                ),
                scanner,
                Messages.get("Error.Validation.Required", "float"),
                logScanned
            );
            VehicleType vehicleType = Vehicle.fields.vehicleType.promptUntilValid(
                withExample.apply(Messages.get("Vehicle.VehicleType"), v -> v.type()),
                scanner,
                Messages.get(
                    "Error.Validation.MustBeOneOfTheFollowing",
                    "vehicle type",
                    VehicleType.showIndexedList(", ")
                ),
                logScanned
            );
            FuelType fuelType = Vehicle.fields.fuelType.promptUntilValid(
                withExample.apply(Messages.get("Vehicle.FuelType"), v -> v.fuelType()),
                scanner,
                Messages.get(
                    "Error.Validation.MustBeOneOfTheFollowing",
                    "fuel type",
                    FuelType.showIndexedList(", ")
                ),
                logScanned
            );

            return new VehicleCreationSchema(
                name,
                new Coordinates(coordinateX, coordinateY),
                enginePower,
                vehicleType,
                fuelType
            );
        }

        /**
         * Same as {@link VehicleCreationSchema#createFromScanner} but with {@code example} parameter defaulted to {@code null}
         *
         * @see VehicleCreationSchema#createFromScanner(Scanner, VehicleCreationSchema)
         */
        public static VehicleCreationSchema createFromScanner(
            Scanner scanner,
            boolean logScanned
        ) {
            return VehicleCreationSchema.createFromScanner(scanner, null, logScanned);
        }

        public void validate() throws ValidationException {
            Vehicle.fields.name.validate(this.name());
            Vehicle.fields.enginePower.validate(this.enginePower());
            Vehicle.fields.fuelType.validate(this.fuelType());
            Vehicle.fields.vehicleType.validate(this.type());
            Coordinates.fields.x.validate(this.coordinates().x());
            Coordinates.fields.y.validate(this.coordinates().y());
        }
    }
}
