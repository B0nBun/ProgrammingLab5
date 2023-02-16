package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Utils;
import lib.Vehicles;
import lib.exceptions.InvalidArgumentException;
import lib.exceptions.InvalidNumberOfArgumentsException;

public class RemoveByIdCommand implements Command {
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
            Long vehicleId = Long.parseUnsignedLong(arguments[0]);

            var found = vehicles.removeIf(v -> v.id() == vehicleId);

            if (!found) {
                Utils.print(writer, "Vehicle with id " + vehicleId + " not found\n");
            }
        } catch (NumberFormatException err) {
            throw new InvalidArgumentException("id", "id must be an unsigned long integer");
        }
    }

    @Override
    public String[] helpArguments() {
        return new String[] {"id"};
    }

    @Override
    public String helpMessage() {
        return "Remove element with specified 'id'";
    }
}
