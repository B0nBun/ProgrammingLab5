package ru.ifmo.app.shared;

import java.io.Serializable;

import ru.ifmo.app.shared.commands.CommandParameters;

public record ClientRequest<T extends CommandParameters>(
    String commandName,
    T commandParameters
) implements Serializable {
    public static ClientRequest<CommandParameters> uncheckedCast(Object object) {
        @SuppressWarnings("unchecked")
        var result = (ClientRequest<CommandParameters>) object;
        return result;
    }

    public static ClientRequest<CommandParameters> withoutParams(String commandName) {
        return new ClientRequest<CommandParameters>(commandName, CommandParameters.dummy);
    }
}
