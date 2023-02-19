package ru.ifmo.app.lib;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import ru.ifmo.app.lib.entities.Coordinates;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.entities.Vehicle;
import ru.ifmo.app.lib.entities.VehicleType;


public class Vehicles {

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

        public Vehicle generate(long id, LocalDate creationDate) {
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

    private LocalDate creationDate;
    private Utils.Peekable<Long> idGenerator;
    private Deque<Vehicle> collection;

    public Vehicles() {
        this.creationDate = LocalDate.now();
        this.idGenerator = new Utils.Peekable<>(
            Stream.iterate(1l, i -> i + 1).iterator()
        );
        this.collection = new ArrayDeque<>();
    }
    
    public Stream<Vehicle> stream() {
        return this.collection.stream();
    }

    public long peekNextId() {
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
}
