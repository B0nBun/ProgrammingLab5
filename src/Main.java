import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import lib.CommandExecutor;
import lib.Utils;
import lib.Vehicles;
import lib.exceptions.CommandNotFoundException;
import lib.exceptions.CommandParseException;
import lib.exceptions.InvalidArgumentException;

// ВАРИАНТ: 863200

public class Main {
	public static void main(String[] args) throws InvalidArgumentException, CommandParseException, CommandParseException, IOException {
		var scanner = new Scanner(System.in);
		var outputWriter = new OutputStreamWriter(System.out);
		var vehicles = new Vehicles();

		var executor = new CommandExecutor(scanner, outputWriter, vehicles);

		while (true) {
			Utils.print(outputWriter, "Input a command: ");
			var commandString = scanner.nextLine();
			try {
				executor.executeCommandString(commandString);
			} catch (CommandNotFoundException err) {
				Utils.print(outputWriter, "Command '" + commandString + "' not found, input 'help' to see a list of all commands\n");
			}
		}
	}
}
