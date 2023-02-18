package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;

public class AverageEnginePowerCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws IOException {
        Double average = vehicles.stream()
            .map(v -> v.enginePower())
            .collect(Collectors.summarizingDouble(f -> Double.valueOf(f)))
            .getAverage();
        Utils.print(writer, "Engine power average: " + average + "\n");
    }

    @Override
    public String helpMessage() {
        return "Prints out an average value of enginePower field of the collection elements";
    }
}
