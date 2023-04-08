package ru.ifmo.app.shared;

import java.io.Serializable;

public record ClientRequest<T extends Serializable>(
    String commandName,
    T commandParameters
) implements Serializable {
    public static ClientRequest<Serializable> uncheckedCast(Object object) {
        @SuppressWarnings("unchecked")
        var result = (ClientRequest<Serializable>) object;
        return result;
    }

    public static ClientRequest<Serializable> withoutParams(String commandName) {
        return new ClientRequest<Serializable>(commandName, new Serializable() {});
    }
}
