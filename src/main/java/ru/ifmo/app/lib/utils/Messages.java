package ru.ifmo.app.lib.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages {
    private final ResourceBundle bundle;

    public Messages(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String get(String key, Object... args) {
        return MessageFormat.format(this.bundle.getString(key), args);
    }
}
