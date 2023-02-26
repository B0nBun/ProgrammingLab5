package ru.ifmo.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.jdom2.JDOMException;

import ru.ifmo.app.lib.CommandExecutor;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.exceptions.ExitProgramException;


// ВАРИАНТ: 863200

// TODO: Логирование
// TODO: Выводить другие сообщения/логи если команды исполняются скриптом, а не пользователем
// TODO: По разному обрабатывать разных потомков IOException

public class App {
	public static void main(String[] args) {

		File vehiclesXmlFile = null;
		if (args.length > 0) {
			vehiclesXmlFile = new File(args[0]);
		}

		try (
			var vehiclesXmlFileStream = vehiclesXmlFile != null ? new FileInputStream(vehiclesXmlFile) : null;
			var scanner = new Scanner(System.in);
			var outputWriter = new PrintWriter(System.out);
		) {

			Vehicles vehicles = null;
			try {
				if (vehiclesXmlFileStream != null) {
					vehicles = Vehicles.loadFromXml(vehiclesXmlFileStream, outputWriter);
					Utils.print(outputWriter, "Loaded " + vehicles.stream().count() + " elements from provided file\n");
				} else {
					Utils.print(outputWriter, "No xml files in arguments were provided, starting with empty collection..." + "\n");
					vehicles = new Vehicles();
				}
			} catch (JDOMException err) {
				Utils.print(outputWriter, "Couldn't parse xml file '" + vehiclesXmlFile + "': " + err.getMessage() + "\n");
				return;
			}
	
			var executor = new CommandExecutor(scanner, outputWriter, vehicles, vehiclesXmlFile);
	
			try {
				while (true) {
					Utils.print(outputWriter, "> ");
					var commandString = scanner.nextLine();
					try {
						executor.executeCommandString(commandString);
					} catch (ExitProgramException err) {
						System.exit(0);
					}
				}
			} catch (NoSuchElementException err) {
				Utils.print(outputWriter, "Couldn't scan the next line: " + err.getMessage() + "\n");
			} catch (IllegalStateException err) {
				Utils.print(outputWriter, "Illegal state exception: " + err.getMessage() + "\n");
			}
		} catch (IOException err) {
			System.out.println("IOException occured: " + err + "\n");
		}
	}
}
