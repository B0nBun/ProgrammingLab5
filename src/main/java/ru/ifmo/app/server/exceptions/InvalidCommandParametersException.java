package ru.ifmo.app.server.exceptions;

public class InvalidCommandParametersException extends Exception {
  public InvalidCommandParametersException() {
    super();
  }

  public InvalidCommandParametersException(String message) {
    super(message);
  }
}
