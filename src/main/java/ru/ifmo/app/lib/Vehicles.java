package ru.ifmo.app.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.fasterxml.uuid.Generators;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.entities.Coordinates;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.entities.Vehicle;
import ru.ifmo.app.lib.entities.VehicleType;
import ru.ifmo.app.lib.exceptions.ParsingException;
import ru.ifmo.app.lib.utils.Peekable;
import ru.ifmo.app.lib.utils.ValidatedScanner;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.xml.XMLConstants;


public class Vehicles {

    private LocalDate creationDate;
    private Peekable<UUID> idGenerator;
    private Deque<Vehicle> collection;

    public Vehicles(Collection<Vehicle> vehiclesIter, LocalDate creationDate) {
        this.creationDate = creationDate;
        var uuidGenerator = Generators.randomBasedGenerator();
        this.idGenerator = new Peekable<>(
            Stream.iterate(uuidGenerator.generate(), __ -> uuidGenerator.generate()).iterator()
        );
        this.collection = new ArrayDeque<>(vehiclesIter);
    }

    public Vehicles() {
        this(new ArrayList<>(), LocalDate.now());
    }
    
    public Element toXmlElement() {
        var rootVehicles = new Element(VehiclesXmlTag.Vehicles.toString())
            .setAttribute(VehiclesXmlTag.CreationDateAttr.toString(), this.creationDate.toString());
            
        List<Element> vehicleElements = this.stream()
            .map(Vehicle::toXmlElement)
            .toList();
        rootVehicles.addContent(vehicleElements);
            
        return rootVehicles;
    }
    
    public static Vehicles loadFromXml(InputStream xmlInputStream, Writer outputWriter) throws IOException, JDOMException {
        var sax = new SAXBuilder();

        // https://rules.sonarsource.com/java/RSPEC-2755
        // prevent xxe
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        
        Document doc = sax.build(xmlInputStream);

        Element rootElement = doc.getRootElement();
        String creationDateString = rootElement.getAttributeValue(VehiclesXmlTag.CreationDateAttr.toString());
        if (creationDateString == null) {
            creationDateString = LocalDate.now().toString();
            App.logger.warn("Expected '{}' attribute in vehicles collection wasn't found, using current date: {}", VehiclesXmlTag.CreationDateAttr, creationDateString);
        }
        LocalDate creationDate = null;
        try {
            creationDate = LocalDate.parse(creationDateString);
        } catch (DateTimeParseException err) {
            App.logger.error("Couldn't parse the creation date: {}", err.getMessage());
            creationDate = LocalDate.now();
            App.logger.error("Continuing with the current date: {}", creationDate);
        }
        List<Element> vehicleElements = rootElement.getChildren();
        var vehicles = new ArrayList<Vehicle>();

        for (var vehicleElement : vehicleElements) {
            try {
                Vehicle vehicle = Vehicle.fromXmlElement(vehicleElement);
                vehicles.add(vehicle);
            } catch (ParsingException err) {
                App.logger.error("Coulnd't parse one of the elements");
            }
        }

        return new Vehicles(vehicles, creationDate);
    }
    
    public Stream<Vehicle> stream() {
        return this.collection.stream();
    }

    public UUID peekNextId() {
        return this.idGenerator.peek();
    }
    
    public LocalDate peekNextCreationDate() {
        return LocalDate.now();
    }

    public void add(VehicleCreationSchema newVehicle) {
        this.collection.add(newVehicle.generate(
            this.idGenerator.next(),
            this.peekNextCreationDate()
        ));
    }

    public Vehicles mutate(Function<Vehicle, Vehicle> mutator) {
        var newDeque = new ArrayDeque<Vehicle>(this.collection.size());
        for (var vehicle: this.collection) {
            var mutated = mutator.apply(vehicle);
            newDeque.add(mutated);
        }
        this.collection = newDeque;
        return this;
    }

    public boolean removeIf(Predicate<Vehicle> predicate) {
        return this.collection.removeIf(predicate);
    }

    public Vehicles clear() {
        this.collection.clear();
        return this;
    }

    public String collectionType() {
        return this.collection.getClass().getName();
    }

    public LocalDate creationDate() {
        return this.creationDate;
    }

    public static record VehicleCreationSchema(
        String name,
        Coordinates coordinates,
        Float enginePower,
        VehicleType type,
        FuelType fuelType
    ) {
        public VehicleCreationSchema(Vehicle vehicle) {
            this(
                vehicle.name(),
                vehicle.coordinates(),
                vehicle.enginePower(),
                vehicle.type(),
                vehicle.fuelType()
            );
        }

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
        
        public static VehicleCreationSchema createFromScanner(
            Scanner scanner,
            Writer writer,
            VehicleCreationSchema example
        ) throws IOException {
            var vscanner = new ValidatedScanner(scanner, writer);

            BiFunction<String, Object, String> withExample = (str1, exampleAttribute) -> {
                if (example == null) {
                    return str1 + ": ";
                }
                return str1 + " (" + exampleAttribute + ")" + ": ";
            };
            
            String name = vscanner.string(
                withExample.apply("Name", example == null ? null : example.name()),
                Vehicle.validate::name
            );
            Long coordinatesX = vscanner.number(
                Long::parseLong,
                Coordinates.validate::x,
                withExample.apply(
                    "Coordinate X",
                    example == null || example.coordinates() == null ? null : example.coordinates().x()
                ),
                __ -> "Long integer required"
            );
            Integer coordinatesY = vscanner.number(
                Integer::parseInt,
                Coordinates.validate::y,
                withExample.apply("Coordinate Y", example == null || example.coordinates() == null ? null : example.coordinates().y()),
                __ -> "Integer required"
            );
            Float enginePower = vscanner.number(
                Float::parseFloat,
                Vehicle.validate::enginePower,
                withExample.apply("Engine Power", example == null ? null : example.enginePower()),
                __ -> "Float required"
            );
            App.logger.info(VehicleType.showIndexedList(", "));
            VehicleType vehicleType = vscanner.vehicleType(
                withExample.apply("Vehicle Type", example == null ? null : example.type())
            );
            App.logger.info(FuelType.showIndexedList(", "));
            FuelType fuelType = vscanner.fuelType(
                withExample.apply("Fuel Type", example == null ? null : example.fuelType())
            );

            var coordinates = new Coordinates(coordinatesX, coordinatesY);
            return new VehicleCreationSchema(name, coordinates, enginePower, vehicleType, fuelType);
        }
        
        public static VehicleCreationSchema createFromScanner(
            Scanner scanner,
            Writer writer
        ) throws IOException {
            return VehicleCreationSchema.createFromScanner(scanner, writer, null);
        }
    }
}
