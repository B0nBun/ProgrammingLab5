package ru.ifmo.app.shared;

import java.io.Serializable;

public record ClientRequest<T>(
    String commandName,
    T commandParameters
) implements Serializable {
    public static ClientRequest<Object> uncheckedCast(Object object) {
        @SuppressWarnings("unchecked")
        var result = (ClientRequest<Object>) object;
        return result;
    }

    public static ClientRequest<Object> withoutParams(String commandName) {
        return new ClientRequest<Object>(commandName, new Serializable() {});
    }
}
