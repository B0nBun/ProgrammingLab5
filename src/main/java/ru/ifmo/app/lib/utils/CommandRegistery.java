package ru.ifmo.app.lib.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import ru.ifmo.app.lib.Command;

/**
 * Class used to associate names of commands with the Command objects. Was implemented
 * to add "aliases" of the commands, so that different keys could return literally the same Command Object.
 */
public class CommandRegistery {
    private LinkedHashMap<Collection<String>, Command> commandsMap = new LinkedHashMap<>();

    /**
     * Put a command entry in the internal map
     * 
     * @param commandAliases
     * @param command
     * @return {@code this} for method chaining
     */
    public CommandRegistery put(Collection<String> commandAliases, Command command) {
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
    public CommandRegistery put(Command command, String ...commandAliases) {
        return this.put(Arrays.asList(commandAliases), command);
    }

    /**
     * Get a {@link Command} associated with the given name.
     * Traverses all of the keys in the internal {@code Map<Collection<String>, Command>}
     * and if the given key is found in the key-collection, returns the found command.
     * 
     * @param commandName
     * @return Found command
     */
    public Command get(String commandName) {
        for (var entry: this.commandsMap.entrySet()) {
            if (entry.getKey().contains(commandName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Get the entry set of the internal map
     * @return
     */
    public Set<Entry<Collection<String>, Command>> getAllCommands() {
        return this.commandsMap.entrySet();
    }
}
