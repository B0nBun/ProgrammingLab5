package ru.ifmo.app.lib.exceptions;

import ru.ifmo.app.lib.utils.Messages;

public class CommandParseException extends Exception {
    public CommandParseException(String commandString) {
        super(Messages.get("Error.CommandParse", commandString));
    }
}
