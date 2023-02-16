package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Vehicles;

public class UpdateCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws IOException {
        // TODO Auto-generated method stub
        
    }
}
