package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Utils;
import lib.Vehicles;

public class ShowCommand implements Command {
    private class RuntimeIOException extends RuntimeException {
        public IOException iocause;
        public RuntimeIOException(IOException iocause) {
            this.iocause = iocause;
        }
    }

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
        Map<String, Command> commandsMap
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
