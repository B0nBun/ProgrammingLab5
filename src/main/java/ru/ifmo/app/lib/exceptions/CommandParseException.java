package ru.ifmo.app.lib.exceptions;

public class CommandParseException extends Exception {
    public CommandParseException(String commandString) {
        super("Couldn't parse a command '" + commandString + "'");
    }
}
