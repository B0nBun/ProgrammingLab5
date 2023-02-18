package ru.ifmo.app;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;


import ru.ifmo.app.lib.CommandExecutor;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.entities.Coordinates;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.entities.VehicleType;
import ru.ifmo.app.lib.exceptions.ExitProgramException;


// ВАРИАНТ: 863200

// TODO: Добавить обработку файла, из которого будет загружаться коллекция по умолчанию
	// Путь к файлу должен быть в переменнах среды
	// Сама коллекция должна хранится в xml
// TODO: save

// TODO: Нормальные сообщения при ошибке парсинга

// TODO: Сгенерировать javadoc
// TODO: Придумать способ для обработки алиасов
// TODO: Использовать Peekable Итератор/Генератор/Stream для генерации ID
// TODO: Обрабатывать ошибки и вывод текста по-другому, если команды выполняются скриптом
// TODO: Парсить энумерацию вне зависимости от регистра текста


public class App {
	public static void main(String[] args) {
		var scanner = new Scanner(System.in); 
		var outputWriter = new OutputStreamWriter(System.out);
		var vehicles = new Vehicles();
		vehicles.add(new Vehicles.VehicleCreationSchema(
			"testName",
			new Coordinates(123l, 321),
			1.5f,
			VehicleType.BICYCLE,
			FuelType.ALCOHOL
		));

		var executor = new CommandExecutor(scanner, outputWriter, vehicles);

		try {
			try {
				while (true) {
					Utils.print(outputWriter, "Input a command: ");
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
			System.out.println("Couldn't write to the output. IOException occured: " + err.getMessage());
		} 
	}
}
