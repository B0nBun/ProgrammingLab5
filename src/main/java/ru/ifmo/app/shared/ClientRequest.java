package ru.ifmo.app.shared;

import java.io.Serializable;
import ru.ifmo.app.shared.commands.CommandParameters;

public record ClientRequest<T extends CommandParameters, Y extends Serializable>(
    String commandName,
    String pathToSavefile,
    T commandParameters,
    Y additionalObject
)
    implements Serializable {
    public static ClientRequest<CommandParameters, Serializable> uncheckedCast(
        Object object
    ) {
        @SuppressWarnings("unchecked")
        var result = (ClientRequest<CommandParameters, Serializable>) object;
        return result;
    }

    public static ClientRequest<CommandParameters, Serializable> withoutParams(
        String commandName,
        String pathToSavefile
    ) {
        return new ClientRequest<CommandParameters, Serializable>(
            commandName,
            pathToSavefile,
            CommandParameters.dummy,
            SerializableDummy.singletone
        );
    }
}
