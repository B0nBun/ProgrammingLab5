package ru.ifmo.app.lib;

import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.exceptions.ParsingException;
import ru.ifmo.app.lib.utils.Messages;


public class Utils {

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

    @FunctionalInterface
    public static interface Validator<T> {
        Optional<String> validate(T value);
    }

    @FunctionalInterface
    public static interface NumberParser<N> {
        N parse(String string) throws NumberFormatException;
    }

    @FunctionalInterface
    public static interface ParsingFunction<T> {
        T tryToParse(String string) throws ParsingException;
    }
}
