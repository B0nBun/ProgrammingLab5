package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Utils;
import lib.Vehicles;
import lib.entities.Vehicle;

public class MinByNameCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws IOException {
        Vehicle minVehicle = vehicles.stream()
            .min((v1, v2) -> v1.name().compareTo(v2.name()))
            .orElse(null);
        
        if (minVehicle == null) {
            Utils.print(writer, "The collection is empty, so there is no min element\n");
            return;
        }
        Utils.print(writer, minVehicle.toString() + "\n");
    }

    @Override
    public String helpMessage() {
        return "Prints out the collection element with a minimal 'name' field";
    }
}
