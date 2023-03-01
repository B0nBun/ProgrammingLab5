package ru.ifmo.app.lib.exceptions;

/**
 * Thrown to indicate that the parsing processs of some sort failed.
 */
public class ParsingException extends Exception {
    public ParsingException(String message) {
        super(message);
    }
}
