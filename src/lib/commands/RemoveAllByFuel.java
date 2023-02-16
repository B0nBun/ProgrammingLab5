package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Utils;
import lib.Vehicles;
import lib.entities.FuelType;
import lib.exceptions.InvalidArgumentException;
import lib.exceptions.InvalidNumberOfArgumentsException;
import lib.exceptions.ParsingException;
import lib.exceptions.RuntimeIOException;

public class RemoveAllByFuel implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws InvalidArgumentException, InvalidNumberOfArgumentsException, IOException {
        if (arguments.length < 1)
            throw new InvalidNumberOfArgumentsException(1, arguments.length);

        try {
            FuelType selectedType = FuelType.parse(arguments[0]);

            vehicles.removeIf(vehicle -> {
                if (vehicle.fuelType() == selectedType) {
                    try {
                        Utils.print(writer, "Removing vehicle with id = " + vehicle.id() + "\n");
                    } catch (IOException err) {
                        throw new RuntimeIOException(err);
                    }
                    return true;
                }
                return false;
            });
        } catch (ParsingException err) {
            throw new InvalidArgumentException("fuelType", "fuelType must be one of the following: " + FuelType.showIndexedList(", "));
        }
    }

    @Override
    public String[] helpArguments() {
        return new String[] {"fuelType"};
    }
    
    @Override
    public String helpMessage() {
        return "Remove all of the elements with the specified fuel type";
    }
}
