package ru.ifmo.app.shared;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import ru.ifmo.app.shared.commands.AddCommand;
import ru.ifmo.app.shared.commands.AddIfMaxCommand;
import ru.ifmo.app.shared.commands.ClearCommand;
import ru.ifmo.app.shared.commands.Command;
import ru.ifmo.app.shared.commands.CountGreaterThanFuelTypeCommand;
import ru.ifmo.app.shared.commands.ExitCommand;
import ru.ifmo.app.shared.commands.FilterGreaterThanFuelTypeCommand;
import ru.ifmo.app.shared.commands.GroupCountingByIdCommand;
import ru.ifmo.app.shared.commands.HeadCommand;
import ru.ifmo.app.shared.commands.HelpCommand;
import ru.ifmo.app.shared.commands.InfoCommand;
import ru.ifmo.app.shared.commands.RemoveByIdCommand;
import ru.ifmo.app.shared.commands.ShowCommand;

/**
 * Commands TODO:
 * remove lower;
 * update;
 * x execute script;
 * x save;
 */

public class CommandRegistery {

    public static CommandRegistery global = new CommandRegistery()
        .put(new HelpCommand(), "h", "help")
        .put(new RemoveByIdCommand(), "remove-by-id", "r")
        .put(new ExitCommand(), "exit", "e", "q")
        .put(new ShowCommand(), "show", "s")
        .put(new ClearCommand(), "clear")
        .put(new CountGreaterThanFuelTypeCommand(), "count-greater-than-fuel-type")
        .put(new InfoCommand(), "info")
        .put(new FilterGreaterThanFuelTypeCommand(), "filter-greater-than-fuel-type")
        .put(new GroupCountingByIdCommand(), "group-counting-by-id")
        .put(new HeadCommand(), "head")
        .put(new AddCommand(), "add", "a")
        .put(new AddIfMaxCommand(), "add-if-max");

    private LinkedHashMap<Collection<String>, Command> commandsMap = new LinkedHashMap<>();

    /**
     * Put a command entry in the internal map
     *
     * @param commandAliases
     * @param command
     * @return {@code this} for method chaining
     */
    private CommandRegistery put(Collection<String> commandAliases, Command command) {
        this.commandsMap.put(commandAliases, command);
        return this;
    }

    /**
     * Put a command entry in the internal map
     *
     * @param commandAliases
     * @param command
     * @return {@code this} for method chaining
     */
    private CommandRegistery put(Command command, String... commandAliases) {
        return this.put(Arrays.asList(commandAliases), command);
    }

    /**
     * Get a {@link DeprecatedCommand} associated with the given name. Traverses all of the keys in
     * the internal {@code Map<Collection<String>, Command>} and if the given key is found in the
     * key-collection, returns the found command.
     *
     * @param commandName
     * @return Found command
     */
    public Command get(String commandName) {
        for (var entry : this.commandsMap.entrySet()) {
            if (entry.getKey().contains(commandName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Get the entry set of the internal map
     *
     * @return
     */
    public Set<Entry<Collection<String>, Command>> getAllCommands() {
        return this.commandsMap.entrySet();
    }
}
