package ru.ifmo.app.lib;

import java.io.Serializable;

public record ServerResponse(
    boolean failed,
    String output
) implements Serializable {
    public static ServerResponse success(String output) {
        return new ServerResponse(false, output);
    }

    public static ServerResponse error(String output) {
        return new ServerResponse(true, output);
    }
}
