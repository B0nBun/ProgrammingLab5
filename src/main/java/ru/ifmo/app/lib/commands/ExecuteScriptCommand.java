package ru.ifmo.app.lib.commands;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandExecutor;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;

public class ExecuteScriptCommand implements Command{
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer,
        Map<String, Command> commandsMap
    ) throws InvalidArgumentException, InvalidNumberOfArgumentsException, IOException, ExitProgramException {
        if (arguments.length < 1)
            throw new InvalidNumberOfArgumentsException(1, arguments.length);

        String scriptFilepath = arguments[0];

        try(Scanner fileScanner = new Scanner(new FileInputStream(scriptFilepath))) {
            var commandExecutor = new CommandExecutor(fileScanner, writer, vehicles);
            
            while (fileScanner.hasNextLine()) {
                String commandString = fileScanner.nextLine();
                commandExecutor.executeCommandString(commandString);
            }
        } catch (FileNotFoundException err) {
            throw new InvalidArgumentException("filepath", "provided file is not found");
        } catch (SecurityException err) {
            throw new InvalidArgumentException("filepath", "provided file denied read access");
        }
    }

    @Override
    public String[] helpArguments() {
        return new String[] {"filepath"};
    }
    
    @Override
    public String helpMessage() {
        return "Executes commands from the provided file";
    }
}
