import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Scanner;

import lib.entities.Coordinates;
import lib.entities.FuelType;
import lib.entities.Vehicle;
import lib.entities.VehicleType;
import lib.exceptions.InvalidArgumentException;

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
	public static void main(String[] args) throws InvalidArgumentException {
		Vehicle foo = new Vehicle(
			123,
			"Name",
			new Coordinates(123, 123l),
			LocalDate.now(),
			123.123f,
			VehicleType.BICYCLE,
			FuelType.ALCOHOL
		);

		System.out.println(foo);
	}
}
