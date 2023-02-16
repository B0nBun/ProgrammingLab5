package lib;

import java.time.LocalDate;
import java.util.LinkedList;
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

    public Vehicles removeIf(Predicate<Vehicle> predicate) {
        this.list.removeIf(predicate);
        return this;
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
