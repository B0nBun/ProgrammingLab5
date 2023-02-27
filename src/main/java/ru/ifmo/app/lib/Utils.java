package ru.ifmo.app.lib;

import java.io.Writer;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.exceptions.ParsingException;


public class Utils {

    public static ParsingException xmlElementParsingException(
        VehiclesXmlTag xmlTag,
        String vehicleUUID,
        String message
    ) {
        return new ParsingException("'" + xmlTag + "' element of vehicle '" + vehicleUUID + "':" + message);
    }

    public static ParsingException xmlElementParsingException(
        VehiclesXmlTag tagName,
        String vehicleUUID,
        ParsingException exception
    ) {
        return xmlElementParsingException(tagName, vehicleUUID, exception.getMessage());
    }

    public static ParsingException xmlAttributeParsingException(
        VehiclesXmlTag tagName,
        String vehicleUUID,
        String message
    ) {
        return new ParsingException("'" + tagName + "' attribute of vehicle '" + vehicleUUID + "':" + message);
    }
    
    public static <T> T scanUntilValid(
        ParsingFunction<T> parsingFunction,
        Validator<T> validator,
        Scanner scanner,
        Writer writer,
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
                App.logger.error("Couldn't parse: {}", parsingErrorMessage.apply(exception));
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
