package lib;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import java.io.IOException;
import java.io.Writer;
import java.util.AbstractMap.SimpleEntry;

import lib.commands.AddCommand;
import lib.commands.HelpCommand;
import lib.commands.InfoCommand;
import lib.commands.RemoveByIdCommand;
import lib.commands.ShowCommand;
import lib.commands.UpdateCommand;
import lib.exceptions.CommandNotFoundException;
import lib.exceptions.CommandParseException;
import lib.exceptions.InvalidArgumentException;
import lib.exceptions.InvalidNumberOfArgumentsException;

public class CommandExecutor {
    private Scanner scanner;
    private Writer writer;
    private Vehicles vehicles;

    public CommandExecutor(
        Scanner scanner,
        Writer writer,
        Vehicles vehicles
    ) {
        this.scanner = scanner;
        this.writer = writer;
        this.vehicles = vehicles; 
    }
    
    private static SimpleEntry<String, String[]> parseCommandString(String commandString) throws CommandParseException {
        String[] splitted = commandString.split("\s+");
        if (splitted.length == 0 || splitted[0].length() == 0) {
            throw new CommandParseException(commandString);
        }
        
        var command = splitted[0];
        var arguments = Arrays.copyOfRange(splitted, 1, splitted.length);
        
        return new SimpleEntry<>(command, arguments);
    }

    public void executeCommandString(String commandString) throws CommandParseException, CommandNotFoundException, InvalidArgumentException, InvalidNumberOfArgumentsException, IOException {
        var pair = CommandExecutor.parseCommandString(commandString);
        var commandname = pair.getKey();
        var arguments = pair.getValue();

        Map<String, Command> commandsMap = new LinkedHashMap<>();
        commandsMap.put("help", new HelpCommand());
        commandsMap.put("info", new InfoCommand());
        commandsMap.put("add",  new AddCommand());
        commandsMap.put("show", new ShowCommand());
        commandsMap.put("update", new UpdateCommand());
        commandsMap.put("remove_by_id", new RemoveByIdCommand());

        Command command = commandsMap.get(commandname);
        if (command == null) {
            throw new CommandNotFoundException(commandname);
        }

        command.execute(arguments, this.vehicles, this.scanner, this.writer, commandsMap);
    }
}
