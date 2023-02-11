import java.time.LocalDate;
import java.util.LinkedList;

import lib.Vehicles;
import lib.entities.Coordinates;
import lib.entities.FuelType;
import lib.entities.Vehicle;
import lib.entities.VehicleType;
import lib.exceptions.InvalidArgumentException;

// ВАРИАНТ: 863200

/* 
	TODO:
	Через DI передавать наиболее абстрактный поток ввода/вывода
	BufferedReader для ввода, (и наверное BufferedWriter для вывода)
	В итоге сигнатура Command.execute должна представлять из себя что-то вроде
	(List<Element> contex, String[] args, Readable output, Writable input) -> List<Element>
		throws ... не знаю как нормально представить ошибки, возможно
					из-за них Readable, Writable организовать не получится
	
	Во всех классах, которые нужно получать через stdin можно объявить
	метод, который и будет за это отвечать
	Element constructFromInput() throws InvalidArgument?

	Для парсинга примитивных данных можно оборачивать BufferedReader в Scanner
*/


public class Main {
	private static String prettyPrintVehicles(Vehicles vehicles) {
		return String.join("\n", vehicles.getAll().stream().map(Vehicle::toString).toList());
	}
	
	public static void main(String[] args) throws InvalidArgumentException {
		Vehicles vehicles = new Vehicles();
		vehicles.add(new Vehicle(
			123,
			"Name",
			new Coordinates(123, 123l),
			LocalDate.now(),
			123.123f,
			VehicleType.BICYCLE,
			FuelType.ALCOHOL
		));

		vehicles.addIfMin(new Vehicle(
			124,
			"Name",
			new Coordinates(123, 123l),
			LocalDate.now(),
			123.123f,
			VehicleType.BICYCLE,
			FuelType.ALCOHOL
		));

		vehicles.add(new Vehicle(
			125,
			"Name",
			new Coordinates(123, 123l),
			LocalDate.now(),
			123.123f,
			VehicleType.BICYCLE,
			FuelType.ALCOHOL
		));

		
		System.out.println(Main.prettyPrintVehicles(vehicles));
	}
}
