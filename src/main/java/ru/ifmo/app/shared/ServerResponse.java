package ru.ifmo.app.shared;

import java.io.Serializable;

public record ServerResponse(String output, Boolean clientDisconnected)
    implements Serializable {}
