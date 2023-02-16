import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import lib.CommandExecutor;
import lib.Vehicles;
import lib.exceptions.CommandNotFoundException;
import lib.exceptions.CommandParseException;
import lib.exceptions.InvalidArgumentException;

// ВАРИАНТ: 863200

// TODO: Handle CommandNotFoundException
public class Main {
	public static void main(String[] args) throws InvalidArgumentException, CommandParseException, CommandParseException, CommandNotFoundException, IOException {
		var scanner = new Scanner(System.in);
		var outputWriter = new OutputStreamWriter(System.out);
		var vehicles = new Vehicles();

		var executor = new CommandExecutor(scanner, outputWriter, vehicles);

		while (true) {
			outputWriter.write("Input a command: ");
			outputWriter.flush();
			var commandString = scanner.nextLine();
			executor.executeCommandString(commandString);
		}
	}
}
