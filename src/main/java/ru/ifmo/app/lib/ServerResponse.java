package ru.ifmo.app.lib;

import java.io.Serializable;

public record ServerResponse(
    String output
) implements Serializable {}
