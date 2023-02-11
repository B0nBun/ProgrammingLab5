package lib;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Predicate;

import lib.entities.Vehicle;

// TODO: Автоматическая генерация id и LocalDate
public class Vehicles {
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
    
    public void add(Vehicle newVehicle) {
        this.list.add(newVehicle);
    }

    public boolean addIfMin(Vehicle newVehicle) {
        Vehicle minVehicle = this.findMin(Vehicle::compareTo);
        if (minVehicle.compareTo(newVehicle) > 0) {
            this.list.add(newVehicle);
            return true;
        }
        return false;
    }
    
    public boolean update(long id, Vehicle updated) {
        boolean listChanged = false;
        for (int i = 0; i < this.list.size(); i ++) {
            if (this.list.get(i).id() == id) {
                listChanged = true;
                this.list.set(i, updated);
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
