package ru.ifmo.app.local.lib.exceptions;

import ru.ifmo.app.shared.utils.Messages;

/**
 * Thrown to indicate that some argument of the command doesn't represent the necessary type or
 * doesn't satisfy any other requirements (e.g. passed "asdasd", when Integer was required)
 */
public class InvalidArgumentException extends Exception {

    public InvalidArgumentException(String argumentName, String message) {
        super(Messages.get("Error.InvalidArgument", argumentName, message));
    }
}
