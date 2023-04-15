package ru.ifmo.app.shared.commands;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class CommandParameters implements Serializable {

    public static final CommandParameters dummy = new CommandParameters() {};

    public static final String description(Class<? extends CommandParameters> clazz) {
        var paramsDescs = new ArrayList<String>();
        for (var field : clazz.getDeclaredFields()) {
            var annotation = field.getAnnotation(CommandParameter.class);
            if (annotation == null) continue;
            paramsDescs.add(annotation.name() + ": " + annotation.desc());
        }
        if (paramsDescs.size() == 0) return "No parameters";
        return String.join("\n", paramsDescs);
    }
}
