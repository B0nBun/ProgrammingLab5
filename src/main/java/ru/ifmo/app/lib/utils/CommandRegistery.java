package ru.ifmo.app.lib.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import ru.ifmo.app.lib.Command;

public class CommandRegistery {
    private LinkedHashMap<Collection<String>, Command> commandsMap = new LinkedHashMap<>();

    public CommandRegistery put(Collection<String> commandAliases, Command command) {
        this.commandsMap.put(commandAliases, command);
        return this;
    }
    
    public CommandRegistery put(String commandName, Command command) {
        return this.put(Arrays.asList(commandName), command);
    }

    public CommandRegistery put(Command command, String ...commandAliases) {
        return this.put(Arrays.asList(commandAliases), command);
    }

    public Command get(String commandName) {
        for (var entry: this.commandsMap.entrySet()) {
            if (entry.getKey().contains(commandName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Set<Entry<Collection<String>, Command>> getAllCommands() {
        return this.commandsMap.entrySet();
    }
}
