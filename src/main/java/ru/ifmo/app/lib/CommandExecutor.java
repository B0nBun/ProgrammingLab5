package ru.ifmo.app.lib;

import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.AbstractMap.SimpleEntry;

import ru.ifmo.app.App;
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
import ru.ifmo.app.lib.commands.SaveCommand;
import ru.ifmo.app.lib.commands.ShowCommand;
import ru.ifmo.app.lib.commands.UpdateCommand;
import ru.ifmo.app.lib.exceptions.CommandParseException;
import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.utils.CommandRegistery;

public class CommandExecutor {
    private Scanner scanner;
    private Writer writer;
    private Vehicles vehicles;
    private File vehiclesFile;
    private CommandRegistery commandRegistery;

    public CommandExecutor(
        Scanner scanner,
        Writer writer,
        Vehicles vehicles,
        File vehiclesFile
    ) {
        this.scanner = scanner;
        this.writer = writer;
        this.vehicles = vehicles; 
        this.vehiclesFile = vehiclesFile;

        this.commandRegistery = new CommandRegistery()
            .put(new HelpCommand(), "help", "h")
            .put(new InfoCommand(), "info", "i")
            .put(new AddCommand(), "add", "a")
            .put(new ShowCommand(), "show", "s")
            .put(new UpdateCommand(), "update", "u")
            .put(new RemoveByIdCommand(), "remove_by_id", "r")
            .put(new ClearCommand(), "clear")
            .put(new ExitCommand(), "exit", "e", "q")
            .put(new HeadCommand(), "head")
            .put(new AddIfMaxCommand(), "add_if_min")
            .put(new RemoveLowerCommand(), "remove_lower")
            .put(new ExecuteScriptCommand(), "execute_script")
            .put(new CountGreaterThanFuelTypeCommand(), "count_greater_than_fuel_type")
            .put(new FilterGreaterThanFuelTypeCommand(), "filter_greater_than_fuel_type")
            .put(new GroupCountingByIdCommand(), "group_counting_by_id")
            .put(new SaveCommand(), "save");
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
        if (commandString.trim().length() == 0) return;
        try {
            var pair = CommandExecutor.parseCommandString(commandString);
            var commandname = pair.getKey();
            var arguments = pair.getValue();
    
            Command command = this.commandRegistery.get(commandname);
            if (command == null) {
                App.logger.warn("Command '{}' not found, input 'help' to see a list of all commands", commandString);
                return;
            }
    
            try {
                command.execute(new CommandContext(
                    arguments,
                    this.vehicles,
                    this.vehiclesFile,
                    this.scanner,
                    this.writer,
                    this.commandRegistery
                ));
            } catch (InvalidNumberOfArgumentsException | InvalidArgumentException err) {
                App.logger.error(err.getMessage());
            }
        } catch (CommandParseException err) {
            App.logger.error("Couldn't parse the command: '{}'", commandString);
        }
    }
}
