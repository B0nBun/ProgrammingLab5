package ru.ifmo.app.lib.exceptions;

import ru.ifmo.app.lib.utils.Messages;

/**
 * Thrown to indicate that given commandString couldn't be parsed and
 * seperated to command name and it's arguments
 */
public class CommandParseException extends Exception {
    public CommandParseException(String commandString) {
        super(Messages.get("Error.CommandParse", commandString));
    }
}
