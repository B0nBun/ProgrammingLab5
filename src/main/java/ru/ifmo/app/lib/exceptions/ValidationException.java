package ru.ifmo.app.lib.exceptions;

/** Thrown to indicate that the validation processs of some sort failed. */
public class ValidationException extends Exception {
  public ValidationException(String message) {
    super(message);
  }
}
