package ru.ifmo.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.jdom2.JDOMException;

import ru.ifmo.app.lib.CommandExecutor;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.exceptions.ParsingException;


// ВАРИАНТ: 863200

// TODO: Добавить обработку файла, из которого будет загружаться коллекция по умолчанию
	// Путь к файлу должен быть в переменнах среды
	// Сама коллекция должна хранится в xml
// TODO: save

// TODO: Логирование
// TODO: Выводить другие сообщения/логи если команды исполняются скриптом, а не пользователем

public class App {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in); 
		Writer outputWriter = new PrintWriter(System.out);
		URL testingFile = Test.class.getClassLoader().getResource("testing.xml");
		try (var vehiclesStream = testingFile.openStream()) {

			Vehicles vehicles = null;
			try {
				vehicles = Vehicles.loadFromXml(vehiclesStream);
			} catch (JDOMException err) {
				Utils.print(outputWriter, "Couldn't parse xml file 'testing.xml': " + err.getMessage());
				return;
			} catch (ParsingException err) {
				// TODO: Continue parsing despite invalid data
				Utils.print(outputWriter, "Parsing exception occured: " + err.getMessage());
				return;
			}
	
			var executor = new CommandExecutor(scanner, outputWriter, vehicles);
	
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
				Utils.print(outputWriter, "Couldn't scan the next line: " + err.getMessage());
			} catch (IllegalStateException err) {
				Utils.print(outputWriter, "Illegal state exception: " + err.getMessage());
			}
		} catch (IOException err) {
			try {
				Utils.print(outputWriter, "IOException occured: " + err);
			} catch (IOException exc) {
				System.out.println("IOException occured: " + exc);
			}
		}
	}
}
