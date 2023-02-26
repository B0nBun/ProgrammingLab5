package ru.ifmo.app.lib;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

import ru.ifmo.app.lib.exceptions.ParsingException;


public class Utils {

    public static ParsingException xmlElementParsingException(
        String elementName,
        String vehicleUUID,
        String message
    ) {
        return new ParsingException("'" + elementName + "' element of vehicle '" + vehicleUUID + "':" + message);
    }

    public static ParsingException xmlElementParsingException(
        String elementName,
        String vehicleUUID,
        ParsingException exception
    ) {
        return xmlElementParsingException(elementName, vehicleUUID, exception.getMessage());
    }

    public static ParsingException xmlAttributeParsingException(
        String attributeName,
        String vehicleUUID,
        String message
    ) {
        return new ParsingException("'" + attributeName + "' attribute of vehicle '" + vehicleUUID + "':" + message);
    }
    
    public static void print(Writer writer, String string) throws IOException {
        writer.write(string);
        writer.flush();
    }
    
    public static <T> T scanUntilValid(
        ParsingFunction<T> parsingFunction,
        Validator<T> validator,
        Scanner scanner,
        Writer writer,
        String inputString,
        Function<ParsingException, String> parsingErrorMessage
    ) throws IOException {
        while (true) {
            print(writer, inputString);
            String line = scanner.nextLine().trim();

            if (line.length() == 0) {
                var validationError = validator.validate(null);
                if (validationError.isEmpty())
                    return null;
                print(writer, validationError.get() + "\n");
                continue;
            }
            
            try {
                var result = parsingFunction.tryToParse(line);
                var validationError = validator.validate(result);
                if (validationError.isEmpty())
                    return result;
                print(writer, validationError.get() + "\n");
            } catch (ParsingException exception) {
                print(writer, "Couldn't parse: " + parsingErrorMessage.apply(exception) + "\n");
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
