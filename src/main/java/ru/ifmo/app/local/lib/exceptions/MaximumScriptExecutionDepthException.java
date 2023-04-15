package ru.ifmo.app.local.lib.exceptions;

public class MaximumScriptExecutionDepthException extends Exception {

    public final int maximumDepth;

    public MaximumScriptExecutionDepthException(int maximumDepth) {
        this.maximumDepth = maximumDepth;
    }
}
