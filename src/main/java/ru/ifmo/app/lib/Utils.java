package ru.ifmo.app.lib;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.entities.VehicleType;
import ru.ifmo.app.lib.exceptions.ParsingException;


public class Utils {
    public static class Peekable<T> implements Iterator<T> {
        private Iterator<T> iterator;
        private Optional<T> nextElement;

        public Peekable(Iterator<T> iterator) {
            this.iterator = iterator;
            this.nextElement = this.iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
        }

        public Peekable(Iterable<T> iterable) {
            this(iterable.iterator());
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        public T next() throws NoSuchElementException {
            var result = this.nextElement.orElseGet(this.iterator::next);
            this.nextElement = iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
            return result;
        }
        
        public T peek() throws NoSuchElementException {
            return nextElement.orElseGet(iterator::next);
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
    
    public static void print(Writer writer, String string) throws IOException {
        writer.write(string);
        writer.flush();
    }

    @FunctionalInterface
    static interface ParsingFunction<T> {
        T tryToParse(String string) throws ParsingException;
    }
    
    public static <T> T scanUntilValid(
        ParsingFunction<T> parsingFunction,
        Validator<T> validator,
        Scanner scanner,
        Writer writer,
        String inputString
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
                print(writer, "Couldn't parse: " + exception.getMessage() + "\n");
            }
        }
    }

    public static record ValidatedScanner(
        Scanner scanner,
        Writer writer
    ) {
        public String string(String inputString, Validator<String> validator) throws IOException {
            return Utils.scanUntilValid(line -> line, validator, scanner, writer, inputString);
        }

        public <T> T number(NumberParser<T> numberParser, Validator<T> validator, String inputString) throws IOException {
            return Utils.scanUntilValid(
                string -> {
                    try {
                        return numberParser.parse(string);
                    } catch (NumberFormatException err) {
                        throw new ParsingException(err.getMessage());
                    }
                },
                validator, scanner, writer, inputString
            );
        }

        public VehicleType vehicleType(String inputString) throws IOException {
            VehicleType type = Utils.scanUntilValid(
                VehicleType::parse,
                (__) -> Optional.empty(),
                scanner, writer, inputString
            );
            return type;
        }

        public FuelType fuelType(String inputString) throws IOException {
            FuelType type = Utils.scanUntilValid(
                FuelType::parse,
                (__) -> Optional.empty(),
                scanner, writer, inputString
            );
            return type;
        }
    }
}
