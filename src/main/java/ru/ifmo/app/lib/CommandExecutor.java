package ru.ifmo.app.lib;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import java.io.IOException;
import java.io.Writer;
import java.util.AbstractMap.SimpleEntry;

import ru.ifmo.app.lib.commands.AddCommand;
import ru.ifmo.app.lib.commands.AddIfMaxCommand;
import ru.ifmo.app.lib.commands.ClearCommand;
import ru.ifmo.app.lib.commands.CountGreaterThanFuelTypeCommand;
import ru.ifmo.app.lib.commands.ExecuteScriptCommand;
import ru.ifmo.app.lib.commands.ExitCommand;
import ru.ifmo.app.lib.commands.FilterGreaterThanFuelTypeCommand;
import ru.ifmo.app.lib.commands.GroupCountingByIdCommand;
import ru.ifmo.app.lib.commands.HeadCommand;
import ru.ifmo.app.lib.commands.HelpCommand;
import ru.ifmo.app.lib.commands.InfoCommand;
import ru.ifmo.app.lib.commands.RemoveByIdCommand;
import ru.ifmo.app.lib.commands.RemoveLowerCommand;
import ru.ifmo.app.lib.commands.ShowCommand;
import ru.ifmo.app.lib.commands.UpdateCommand;
import ru.ifmo.app.lib.exceptions.CommandParseException;
import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;

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
        String[] splitted = commandString.trim().split("\s+");
        if (splitted.length == 0 || splitted[0].length() == 0) {
            throw new CommandParseException(commandString);
        }
        
        var command = splitted[0];
        var arguments = Arrays.copyOfRange(splitted, 1, splitted.length);
        
        return new SimpleEntry<>(command, arguments);
    }

    public void executeCommandString(String commandString) throws IOException, ExitProgramException {
        try {
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
            commandsMap.put("clear", new ClearCommand());
            commandsMap.put("exit", new ExitCommand());
            commandsMap.put("head", new HeadCommand());
            commandsMap.put("add_if_min", new AddIfMaxCommand());
            commandsMap.put("remove_lower", new RemoveLowerCommand());
            commandsMap.put("execute_script", new ExecuteScriptCommand());
            commandsMap.put("count_greater_than_fuel_type", new CountGreaterThanFuelTypeCommand());
            commandsMap.put("filter_greater_than_fuel_type", new FilterGreaterThanFuelTypeCommand());
            commandsMap.put("group_counting_by_id", new GroupCountingByIdCommand());
    
            Command command = commandsMap.get(commandname);
            if (command == null) {
                Utils.print(writer, "Command '" + commandString + "' not found, input 'help' to see a list of all commands\n");
                return;
            }
    
            try {
                command.execute(arguments, this.vehicles, this.scanner, this.writer, commandsMap);
            } catch (InvalidNumberOfArgumentsException | InvalidArgumentException err) {
                Utils.print(writer, err.getMessage() + "\n");
            }
        } catch (CommandParseException err) {
            Utils.print(writer, "Couldn't parse the command: \"" + commandString + "\"\n");
        }
    }
}
