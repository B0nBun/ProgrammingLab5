package ru.ifmo.app.lib;

import java.io.Serializable;

public record ClientMessage(
    String message,
    Integer value
) implements Serializable {}
