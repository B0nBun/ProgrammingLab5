package ru.ifmo.app.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.time.LocalDate;
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

import ru.ifmo.app.lib.entities.Coordinates;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.entities.Vehicle;
import ru.ifmo.app.lib.entities.VehicleType;
import ru.ifmo.app.lib.exceptions.ParsingException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.xml.XMLConstants;


public class Vehicles {

    private LocalDate creationDate;
    private Utils.Peekable<UUID> idGenerator;
    private Deque<Vehicle> collection;

    public Vehicles(Collection<Vehicle> vehiclesIter, LocalDate creationDate) {
        this.creationDate = creationDate;
        var uuidGenerator = Generators.randomBasedGenerator();
        this.idGenerator = new Utils.Peekable<>(
            Stream.iterate(uuidGenerator.generate(), __ -> uuidGenerator.generate()).iterator()
        );
        this.collection = new ArrayDeque<>(vehiclesIter);
    }

    public Vehicles() {
        this(new ArrayList<>(), LocalDate.now());
    }

    public static Vehicles loadFromXml(InputStream xmlInputStream) throws IOException, JDOMException, ParsingException {
        var sax = new SAXBuilder();

        // https://rules.sonarsource.com/java/RSPEC-2755
        // prevent xxe
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        
        Document doc = sax.build(xmlInputStream);

        // TODO: Обработать creation-date атрибут
        LocalDate creationDate = LocalDate.now();
        Element rootElement = doc.getRootElement();
        List<Element> vehicleElements = rootElement.getChildren();
        var vehicles = new ArrayList<Vehicle>();

        // TODO: Сообщать о невалидных элементах и пропускать их
        for (var vehicleElement : vehicleElements) {
            Vehicle vehicle = Vehicle.fromXmlElement(vehicleElement);
            vehicles.add(vehicle);
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
            var vscanner = new Utils.ValidatedScanner(scanner, writer);

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
            Utils.print(writer, VehicleType.showIndexedList(", ") + "\n");
            VehicleType vehicleType = vscanner.vehicleType(
                withExample.apply("Vehicle Type", example == null ? null : example.type())
            );
            Utils.print(writer, FuelType.showIndexedList(", ") + "\n");
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
