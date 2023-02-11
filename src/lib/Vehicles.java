package lib;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Predicate;

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

    long idCounter = 0;
    private LinkedList<Vehicle> list;

    public Vehicles() {
        this.list = new LinkedList<>();
    }
    
    public LinkedList<Vehicle> getAll() {
        return this.list;
    }

    public Vehicle head() {
        return this.list.getFirst();
    }

    public Vehicle findMin(Comparator<Vehicle> comparator) {
        Vehicle minVehicle = null;
        for (Vehicle vehicle : this.list) {
            if (minVehicle == null || comparator.compare(vehicle, minVehicle) < 0) {
                minVehicle = vehicle;
            }
        }
        return minVehicle;
    }

    public Float enginePowerAverage() {
        Float sum = 0f;
        for (int i = 0; i < this.list.size(); i ++) {
            sum += this.list.get(i).enginePower();
        }
        return sum / this.list.size();
    }
    
    public void add(VehicleCreationSchema newVehicle) {
        this.idCounter ++;
        this.list.add(newVehicle.generate(this.idCounter, LocalDate.now()));
    }

    public boolean addIfMin(VehicleCreationSchema newVehicle) {
        Vehicle minVehicle = this.findMin(Vehicle::compareTo);

        this.idCounter ++;
        Vehicle createdVehicle = newVehicle.generate(this.idCounter, LocalDate.now());
        int comparison = minVehicle.compareTo(createdVehicle);

        if (comparison > 0) {
            this.list.add(createdVehicle);
            return true;
        }
        return false;
    }
    
    public boolean update(long id, VehicleCreationSchema updatedSchema) {
        boolean listChanged = false;
        for (int i = 0; i < this.list.size(); i ++) {
            Vehicle foundVehicle = this.list.get(i);
            if (foundVehicle.id() == id) {
                listChanged = true;
                this.list.set(i, updatedSchema.generate(
                    foundVehicle.id(),
                    foundVehicle.creationDate()
                ));
            }
        }
        return listChanged;
    }

    public boolean removeIf(Predicate<Vehicle> predicate) {
        return this.list.removeIf(predicate);
    }

    public void clear() {
        this.list.clear();
    }
    
    public String serialize() {
        return "TODO";
    }
}
