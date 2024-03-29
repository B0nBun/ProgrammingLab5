package ru.ifmo.app.local.lib.exceptions;

import ru.ifmo.app.shared.utils.Messages;

/**
 * Thrown to indicate the the number of arguments passed to some command doesn't satisfy the
 * required conditions.
 */
public class InvalidNumberOfArgumentsException extends Exception {

    public InvalidNumberOfArgumentsException(int requiredNumber, int providedNumber) {
        super(Messages.get("InvalidNumberOfArguments", requiredNumber, providedNumber));
    }
}
