package ru.ifmo.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ifmo.app.lib.CommandExecutor;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.exceptions.ExitProgramException;


// ВАРИАНТ: 863200

// TODO: Логирование
// TODO: Выводить другие сообщения/логи если команды исполняются скриптом, а не пользователем
// TODO: Убрать захардкоженные строки и перенсти все в файлы конфигурации
// TODO: javadoc
// TODO: Заняться декомпозицией методов
// TODO: Занятся форматированием кода

public class App {

	public static Logger logger = LoggerFactory.getLogger("ru.ifmo.app.logger");
	
	public static void main(String[] args) {

		File vehiclesXmlFile = null;
		if (args.length > 0) {
			vehiclesXmlFile = new File(args[0]);
		}

		try (
			var scanner = new Scanner(System.in);
			var outputWriter = new PrintWriter(System.out);
		) {

			Vehicles vehicles = null;
			try (
				var vehiclesXmlFileStream = vehiclesXmlFile != null ? new FileInputStream(vehiclesXmlFile) : null;
			) {		
				if (vehiclesXmlFileStream != null) {
					vehicles = Vehicles.loadFromXml(vehiclesXmlFileStream, outputWriter);
					App.logger.info("Loaded {} elements from provided file", vehicles.stream().count());
				} else {
					App.logger.warn("No xml files in arguments were provided");
				}
			} catch (JDOMException err) {
				App.logger.error("Couldn't parse xml file '{}': {}", vehiclesXmlFile, err.getMessage());
			} catch (FileNotFoundException err) {
				App.logger.error("File '{}' not found: {}", vehiclesXmlFile, err.getMessage());
			}

			if (vehicles == null) {
				App.logger.warn("Starting with empty collection...");
				vehicles = new Vehicles();
			}
		
			var executor = new CommandExecutor(scanner, outputWriter, vehicles, vehiclesXmlFile);
	
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
				App.logger.error("Couldn't scan the next line: {}", err.getMessage());
			} catch (IllegalStateException err) {
				App.logger.error("Illegal state exception: {}", err.getMessage());
			}
		} catch (IOException err) {
			App.logger.error("IOException occured: {}", err.getMessage());
		}
	}
}
