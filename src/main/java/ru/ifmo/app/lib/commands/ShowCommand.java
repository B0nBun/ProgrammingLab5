package ru.ifmo.app.lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.Utils.CommandRegistery;
import ru.ifmo.app.lib.exceptions.RuntimeIOException;

public class ShowCommand implements Command {

    private class BooleanBox {
        public boolean value;
        public BooleanBox(boolean value) {
            this.value = value;
        }
    }
    
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        CommandRegistery commandsRegistery
    ) throws IOException {
        // Я обажаю джаву :)
        try {
            var stream = vehicles.stream();

            var isEmpty = new BooleanBox(true);            
            stream.forEach(vehicle -> {
                isEmpty.value = false;
                try {
                    Utils.print(writer, vehicle.toString() + "\n");
                } catch (IOException err) {
                    throw new RuntimeIOException(err);
                }
            });

            if (isEmpty.value) {
                Utils.print(writer, "Collection is empty\n");
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
