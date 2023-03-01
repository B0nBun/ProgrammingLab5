package ru.ifmo.app.lib.utils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A class, which is used to get the message strings from "ApplicationMessages.properties" file
 */
public class Messages {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("ApplicationMessages");

    /**
     * Get a message string from the "ApplicationMessages" resource bundle and format
     * it with the given arguments. Gets the string using the {@link ResourceBundle#getString} to
     * get the message and formats it with the {@link MessageFormat#format} method. If any of the methods
     * throw {@link MissingresourceException} or {@link IllegalArgumentException} then returns a string
     * containing given key and a list of arguments (e.g. "NonExistantKey[123, "Other arg"]")
     * 
     * @param key A key of the message from the {@code ApplicationMessages.properties} bundle
     * @param args Arguments, which are used to format the string returned from the bundle
     * @return Formatted string. If the given key is missing or formatting fails then
     *         string contains the key and arguments themselves (e.g. "NonExistantKey[123, "Other arg"]")
     */
    public static String get(String key, Object... args) {
        try {
            return MessageFormat.format(Messages.bundle.getString(key), args);
        } catch (MissingResourceException | IllegalArgumentException err) {
            return key + Arrays.asList(args);
        }
    }
}
