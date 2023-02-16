package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Utils;
import lib.Vehicles;
import lib.entities.Vehicle;

public class HeadCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws IOException {
        Vehicle head = vehicles.stream().sorted().findFirst().orElse(null);
        if (head == null) {
            Utils.print(writer, "The collection is empty, so there is no first element\n");
            return;
        }
        Utils.print(writer, head.toString() + "\n");
    }

    @Override
    public String helpMessage() {
        return "Prints out the first element in the collection";
    }
}