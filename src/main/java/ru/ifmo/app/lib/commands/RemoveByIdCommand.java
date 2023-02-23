package ru.ifmo.app.lib.commands;

import java.io.IOException;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.exceptions.RuntimeIOException;

public class RemoveByIdCommand implements Command {
    @Override
    public void execute(CommandContext context) throws InvalidNumberOfArgumentsException, IOException {
        if (context.arguments().length < 1)
            throw new InvalidNumberOfArgumentsException(1, context.arguments().length);
        
        String vehicleUUID = context.arguments()[0];

        try {
            var found = context.vehicles().removeIf(v -> {
                if (v.id().toString().startsWith(vehicleUUID)) {
                    try {
                        Utils.print(context.writer(), "Removing vehicle with id=" + v.id() + "...\n");
                    } catch (IOException err) {
                        throw new RuntimeIOException(err);
                    }
                    return true;
                }
                return false;
            });
    
            if (!found) {
                Utils.print(context.writer(), "Vehicle with id starting with '" + vehicleUUID + "' not found\n");
            }
        } catch (RuntimeIOException err) {
            throw err.iocause;
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
