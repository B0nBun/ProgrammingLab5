package ru.ifmo.app.lib.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("ApplicationMessages");

    public static String get(String key, Object... args) {
        return MessageFormat.format(Messages.bundle.getString(key), args);
    }
}
