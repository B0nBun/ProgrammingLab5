package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Utils;
import lib.Vehicles;

public class ClearCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws IOException {
        vehicles.clear();        
        Utils.print(writer, "Collection cleared!\n");
    }

    @Override
    public String helpMessage() {
        return "Clears the collection";
    }
}
