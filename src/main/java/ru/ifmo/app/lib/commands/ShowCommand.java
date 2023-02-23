package ru.ifmo.app.lib.commands;

import java.io.IOException;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.exceptions.RuntimeIOException;

public class ShowCommand implements Command {

    private class BooleanBox {
        public boolean value;
        public BooleanBox(boolean value) {
            this.value = value;
        }
    }
    
    @Override
    public void execute(CommandContext context) throws IOException {
        // Я обажаю джаву :)
        try {
            var stream = context.vehicles().stream();

            var isEmpty = new BooleanBox(true);            
            stream.forEach(vehicle -> {
                isEmpty.value = false;
                try {
                    Utils.print(context.writer(), vehicle.toString() + "\n");
                } catch (IOException err) {
                    throw new RuntimeIOException(err);
                }
            });

            if (isEmpty.value) {
                Utils.print(context.writer(), "Collection is empty\n");
            }
        } catch (RuntimeIOException err) {
            throw err.iocause;
        }
    }

    @Override
    public String helpMessage() {
        return "Prints out every element of the collection";
    }
}
