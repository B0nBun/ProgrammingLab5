package ru.ifmo.app.lib;

import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.exceptions.ParsingException;
import ru.ifmo.app.lib.utils.Messages;


/**
 * A class that serves as a namespace for utility methods and functional interfaces
 */
public class Utils {

    /**
     * Using the passed scanner, starts a loop which prmopts the user,
     * scans the next line and breaks the loop only if the provided
     * input is parsed without exceptions and passes the validator.
     * 
     * @param <T> A type of the value, which should be 'gotten' from the scanned input
     * 
     * @param parsingFunction A function that accepts a String and returns a value of a generic type {@code T}
     *                        (can throw a ParsingException to signify that the parsing failed)
     * 
     * @param validator A function that accepts a value of type {@code T} and returns an {@link Optional} of a String.
     *                  See functional interface {@link Validator} for more detailed description
     * 
     * @param scanner A Scanner which is used to get the input by calling the {@link Scanner#nextLine()}
     * 
     * @param inputString A String which is logged before the prompt
     * 
     * @param parsingErrorMessage A function which converts a {@link ParsingException} thrown by
     *                            the {@code parsingFunction} to a string message
     * 
     * @return A value of type {@code T} scanned from the line in the Scanner. This value is guaranteed
     *         to have passed the parsing and validation processes
     */
    public static <T> T scanUntilValid(
        ParsingFunction<T> parsingFunction,
        Validator<T> validator,
        Scanner scanner,
        String inputString,
        Function<ParsingException, String> parsingErrorMessage
    ) {
        while (true) {
            App.logger.info(inputString);
            String line = scanner.nextLine().trim();

            if (line.length() == 0) {
                var validationError = validator.validate(null);
                if (validationError.isEmpty())
                    return null;
                App.logger.error(validationError.get());
                continue;
            }
            
            try {
                var result = parsingFunction.tryToParse(line);
                var validationError = validator.validate(result);
                if (validationError.isEmpty())
                    return result;
                App.logger.error(validationError.get());
            } catch (ParsingException exception) {
                App.logger.error(Messages.get("Error.Parsing", parsingErrorMessage.apply(exception)));
            }
        }
    }

    /**
     * Functional interface which implements the {@code validate} method.
     * See {@link Validator#validate} for more detailed description.
     */
    @FunctionalInterface
    public static interface Validator<T> {
        /**
         * Function, which validates that the value of type {@code T}
         * is correct.
         * <p><i>(I don't remember why I did it with Optional and not with a checked exception)</i></p>
         * 
         * @param value
         * @return An {@link Optional} of a String, which serves as an error signifier.
         *         So if returned optional is empty, then the value is considered validated.
         *         Otherwise, the String in the optional is considered to be an error message.
         */
        Optional<String> validate(T value);
    }

    /**
     * Functional interface which implements the {@code parse} method.
     * See {@link NumberParser#parse} for more detailed description.
     */
    @FunctionalInterface
    public static interface NumberParser<N> {
        /**
         * Method, which parses some kind of the number value from provided String.
         * (It actually can be any type, but {@link NumberFormatException} is what signifies the failed parsing)
         * 
         * @param string A string from which the value should be parsed
         * @return A parsed value
         * @throws NumberFormatException This exception is thrown if the parsing is failed
         */
        N parse(String string) throws NumberFormatException;
    }

    /**
     * Functional interface which implements the {@code tryToParse} method.
     * See {@link ParsingFunction#tryToParse} for more detailed description.
     */
    @FunctionalInterface
    public static interface ParsingFunction<T> {
        /**
         * Method, which parses value of type {@code T} from provided String.
         * 
         * @param string A string from which the value should be parsed
         * @return A parsed value
         * @throws ParsingException This exception is thrown if the parsing is failed
         */
        T tryToParse(String string) throws ParsingException;
    }
}
