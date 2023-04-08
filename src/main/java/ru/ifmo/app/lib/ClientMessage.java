package ru.ifmo.app.lib;

import java.io.Serializable;

public record ClientMessage<T>(
    String commandName,
    T commandParameters
) implements Serializable {
    public static ClientMessage<Object> uncheckedCast(Object object) {
        @SuppressWarnings("unchecked")
        var result = (ClientMessage<Object>) object;
        return result;
    }

    public static ClientMessage<Object> withoutParams(String commandName) {
        return new ClientMessage<Object>(commandName, new Serializable() {});
    }
}
