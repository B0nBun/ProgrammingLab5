package lib;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lib.entities.Coordinates;
import lib.entities.FuelType;
import lib.entities.Vehicle;
import lib.entities.VehicleType;

public class Vehicles {
    /*
        Я понятия не имею как решить проблему разделения автоматический генерируемых
        и указываемых полей. По идеи это не должно зависить от класса Vehicle так как
        он просто является носителем информации, но если отделить CreationSchema и поместить
        ее в другое место, то возникает проблема, что сигнатура ее конструктора зависит от 
        сигнатуры конструктора Vehicle, из-за чего при изменении класса Vehicle полетят
        ошибки. Решения у меня нет и этот комментарий тут как напоминание либо подумать потом,
        либо спросить совета как надо
    */
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
            String name = Utils.scanUntilParsedNonemptyString(scanner, writer,
                "Name (" + example.name() + ")" + ": " 
            );
            Coordinates coordinates = Utils.scanUntilParsedCoordinates(scanner, writer,
                "Coordinates (" + example.coordinates() + "): \n"
            );
            Float enginePower = Utils.scanUntilParsedPositiveFloat(scanner, writer,
                "Engine Power (" + example.enginePower() + "): ",
                false, null
            );
            VehicleType vehicleType = Utils.scanUntilParsedVehicleType(scanner, writer,
                "Vehicle Type (" + example.type() + "): " + VehicleType.showIndexedList(", ") + ": \n",
                true, null
            );
            FuelType fuelType = Utils.scanUntilParsedFuelType(scanner, writer,
                "Fuel Type (" + example.fuelType() + "): " + FuelType.showIndexedList(", ") + ": \n",
                false, null
            );

            return new VehicleCreationSchema(name, coordinates, enginePower, vehicleType, fuelType);
        }
        
        public static VehicleCreationSchema createFromScanner(
            Scanner scanner,
            Writer writer
        ) throws IOException {
            String name = Utils.scanUntilParsedNonemptyString(scanner, writer, "Name: ");
            Coordinates coordinates = Utils.scanUntilParsedCoordinates(scanner, writer, "Coordinates: \n");
            Float enginePower = Utils.scanUntilParsedPositiveFloat(scanner, writer, "Engine Power: ", false, null);
            VehicleType vehicleType = Utils.scanUntilParsedVehicleType(scanner, writer, "Vehicle Type: " + VehicleType.showIndexedList(", ") + ": \n", true, null);
            FuelType fuelType = Utils.scanUntilParsedFuelType(scanner, writer, "Fuel Type: " + FuelType.showIndexedList(", ") + ": \n", false, null);

            return new VehicleCreationSchema(name, coordinates, enginePower, vehicleType, fuelType);
        }
    }

    private LocalDate creationDate;
    private long idCounter = 0;
    private LinkedList<Vehicle> list;

    public Vehicles() {
        this.list = new LinkedList<>();
        this.creationDate = LocalDate.now();
    }
    
    public Stream<Vehicle> stream() {
        return this.list.stream();
    }

    public void add(VehicleCreationSchema newVehicle) {
        this.idCounter ++;
        this.list.add(newVehicle.generate(this.idCounter, LocalDate.now()));
    }

    public Vehicles mutate(Function<Vehicle, Vehicle> mutator) {
        for (int i = 0; i < this.list.size(); i ++) {
            var mutated = mutator.apply(this.list.get(i));
            this.list.set(i, mutated);
        }
        return this;
    }

    public boolean removeIf(Predicate<Vehicle> predicate) {
        return this.list.removeIf(predicate);
    }

    public Vehicles clear() {
        this.list.clear();
        return this;
    }

    public String collectionType() {
        return this.list.getClass().getName();
    }

    public LocalDate creationDate() {
        return this.creationDate;
    }
}
