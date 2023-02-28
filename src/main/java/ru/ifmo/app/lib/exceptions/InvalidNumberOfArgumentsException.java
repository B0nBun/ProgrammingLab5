package ru.ifmo.app.lib.exceptions;

import ru.ifmo.app.lib.utils.Messages;

public class InvalidNumberOfArgumentsException extends Exception {
    public InvalidNumberOfArgumentsException(int requiredNumber, int providedNumber) {
        super(Messages.get("InvalidNumberOfArguments", requiredNumber, providedNumber));
    }
}
