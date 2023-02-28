package ru.ifmo.app.lib.exceptions;

import ru.ifmo.app.lib.utils.Messages;

public class InvalidArgumentException extends Exception {    
    public InvalidArgumentException(
        String argumentName,
        String message
    ) {
        super(Messages.get("Error.InvalidArgument", argumentName, message));
    }
}
