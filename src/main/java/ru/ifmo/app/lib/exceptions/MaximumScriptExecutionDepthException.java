package ru.ifmo.app.lib.exceptions;

public class MaximumScriptExecutionDepthException extends Exception {
  final public int maximumDepth;

  public MaximumScriptExecutionDepthException(int maximumDepth) {
    this.maximumDepth = maximumDepth;
  }
}
