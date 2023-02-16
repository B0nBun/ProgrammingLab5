package lib.commands;

import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import lib.Command;
import lib.Vehicles;
import lib.exceptions.ExitProgramException;

public class ExitCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws ExitProgramException {
        throw new ExitProgramException();
    }

    @Override
    public String helpMessage() {
        return "Exits the program";
    }
}
