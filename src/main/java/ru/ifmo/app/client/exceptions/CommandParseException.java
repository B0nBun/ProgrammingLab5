package ru.ifmo.app.client.exceptions;

/**
 * Thrown to indicate that given commandString couldn't be parsed and seperated to command name and
 * it's arguments
 */
public class CommandParseException extends Exception {
  public CommandParseException(String message) {
    super(message);
  }
}
