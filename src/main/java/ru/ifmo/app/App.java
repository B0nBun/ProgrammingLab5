package ru.ifmo.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ifmo.app.lib.CommandExecutor;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.utils.Messages;


// ВАРИАНТ: 863200

// TODO: Заняться декомпозицией методов (параллельно посмотреть не стоит ли поставить final там, где это возможно)
// TODO: Занятся форматированием кода

/**
 * Entry point of a program, which starts the repl loop and loads the xml file.
 */
public class App {

	/**
	 * Static global logger, which is used across all of the classes.
	 */
	public static Logger logger = LoggerFactory.getLogger("ru.ifmo.app.logger");
	
	/**
	 * Entry point of a program, which starts the repl loop and loads the xml file.
	 * @param args Command line arguments, the first one of which can be an XML file.
	 */
	public static void main(String[] args) {
		File vehiclesXmlFile = null;
		if (args.length > 0) {
			vehiclesXmlFile = new File(args[0]);
		}

		try (
			var scanner = new Scanner(System.in);
		) {

			Vehicles vehicles = null;
			try (
				var vehiclesXmlFileStream = vehiclesXmlFile != null ? new FileInputStream(vehiclesXmlFile) : null;
			) {		
				if (vehiclesXmlFileStream != null) {
					vehicles = Vehicles.loadFromXml(vehiclesXmlFileStream);
					App.logger.info(Messages.get("LoadedElementsFromFile", vehicles.stream().count()));
				} else {
					App.logger.warn(Messages.get("Warn.NoXmlFileInArguments"));
				}
			} catch (JDOMException err) {
				App.logger.error(Messages.get("Error.XmlFileParsing", vehiclesXmlFile, err.getMessage()));
			} catch (FileNotFoundException err) {
				App.logger.error(Messages.get("Error.FileNotFound", vehiclesXmlFile, err.getMessage()));
			}

			if (vehicles == null) {
				App.logger.warn(Messages.get("Warn.EmptyCollectionStart"));
				vehicles = new Vehicles();
			}
		
			var executor = new CommandExecutor(scanner, vehicles, vehiclesXmlFile);
	
			try {
				while (true) {
					System.out.print("> ");
					var commandString = scanner.nextLine();
					try {
						executor.executeCommandString(commandString);
					} catch (ExitProgramException err) {
						System.exit(0);
					}
				}
			} catch (NoSuchElementException err) {
				App.logger.error(Messages.get("Error.NoSuchElement", err.getMessage()));
			} catch (IllegalStateException err) {
				App.logger.error(Messages.get("Error.IllegalState", err.getMessage()));
			}
		} catch (IOException err) {
			App.logger.error(Messages.get("Error.IO", err.getMessage()));
		}
	}
}
