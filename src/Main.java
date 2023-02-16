import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import lib.CommandExecutor;
import lib.Utils;
import lib.Vehicles;
import lib.entities.Coordinates;
import lib.entities.FuelType;
import lib.entities.VehicleType;
import lib.exceptions.CommandNotFoundException;
import lib.exceptions.CommandParseException;
import lib.exceptions.ExitProgramException;
import lib.exceptions.InvalidArgumentException;
import lib.exceptions.InvalidNumberOfArgumentsException;

// ВАРИАНТ: 863200

// TODO: Добавить обработку файла, из которого будет загружаться коллекция по умолчанию
	// Путь к файлу должен быть в переменнах среды
	// Сама коллекция должна хранится в xml
// TODO: save
// TODO: execute_script file_name

// TODO: remove_lower {element}
// TODO: remove_all_by_fuel_type fuelType
// TODO: average_of_engine_power
// TODO: min_by_name

// TODO: Сгенерировать javadoc

// TODO: Обработать CommandParseException
public class Main {
	public static void main(String[] args) throws InvalidArgumentException, CommandParseException, CommandParseException, IOException, ExitProgramException {
		var scanner = new Scanner(System.in);
		var outputWriter = new OutputStreamWriter(System.out);
		var vehicles = new Vehicles();
		vehicles.add(new Vehicles.VehicleCreationSchema(
			"testName",
			new Coordinates(123, 321l),
			1.5f,
			VehicleType.BICYCLE,
			FuelType.ALCOHOL
		));

		var executor = new CommandExecutor(scanner, outputWriter, vehicles);

		while (true) {
			Utils.print(outputWriter, "Input a command: ");
			var commandString = scanner.nextLine();
			try {
				executor.executeCommandString(commandString);
			} catch (CommandNotFoundException err) {
				Utils.print(outputWriter, "Command '" + commandString + "' not found, input 'help' to see a list of all commands\n");
			} catch (InvalidNumberOfArgumentsException | InvalidArgumentException err) {
				Utils.print(outputWriter, err.getMessage() + "\n");
			} catch (ExitProgramException err) {
				System.exit(0);
			}
		}
	}
}
