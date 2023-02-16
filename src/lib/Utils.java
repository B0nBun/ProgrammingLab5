package lib;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lib.entities.Coordinates;
import lib.entities.FuelType;
import lib.entities.VehicleType;
import lib.exceptions.ParsingException;

public class Utils {
    public static void print(Writer writer, String string) throws IOException {
        writer.write(string);
        writer.flush();
    }

    @FunctionalInterface
    static interface ParsingFunction<T> {
        T tryToParse(String string) throws ParsingException;
    }
    
    public static <T> T scanUntilParsed(
        ParsingFunction<T> parsingFunction,
        Scanner scanner,
        Writer writer,
        String inputString,
        String parseErrorString,
        boolean allowEmptyString,
        T defaultValue
    ) throws IOException {
        while (true) {
            print(writer, inputString);
            String line = scanner.nextLine().trim();

            if (allowEmptyString && line.length() == 0)
                return defaultValue;

            try {
                var result = parsingFunction.tryToParse(line);
                return result;
            } catch (ParsingException exception) {
                print(writer, parseErrorString + "\n");
            }
        }
    }
    
    public static String scanUntilParsedNonemptyString(
        Scanner scanner,
        Writer writer,
        String inputString
    ) throws IOException {
        return Utils.scanUntilParsed(
            string -> {
                if (string.length() == 0)
                    throw new ParsingException();
                return string;
            }, scanner, writer, inputString,
            "Nonempty string required!",
            false,
            null
        );
    }
    
    public static Integer scanUntilParsedUnsignedInt(
        Scanner scanner,
        Writer writer,
        String inputString,
        boolean allowEmptyString,
        Integer defaultValue
    ) throws IOException {
        return Utils.scanUntilParsed(
            (string) -> {
                try {
                    Integer result = Integer.parseUnsignedInt(string);
                    return result;
                } catch (NumberFormatException err) {
                    throw new ParsingException();
                }
            }, scanner, writer, inputString, "Unsigned integer requried!", allowEmptyString, defaultValue
        );
    }

    public static Integer scanUntilParsedInt(
        Scanner scanner,
        Writer writer,
        String inputString,
        boolean allowEmptyString,
        Integer defaultValue
    ) throws IOException {
        return Utils.scanUntilParsed(
            (string) -> {
                try {
                    Integer result = Integer.parseInt(string);
                    return result;
                } catch (NumberFormatException err) {
                    throw new ParsingException();
                }
            }, scanner, writer, inputString, "Integer requried!", allowEmptyString, defaultValue
        );
    }

    public static Long scanUntilParsedLong(
        Scanner scanner,
        Writer writer,
        String inputString,
        boolean allowEmptyString,
        Long defaultValue
    ) throws IOException {
        return Utils.scanUntilParsed(
            (string) -> {
                try {
                    Long result = Long.parseLong(string);
                    return result;
                } catch (NumberFormatException err) {
                    throw new ParsingException();
                }
            }, scanner, writer, inputString, "Long integer requried!", allowEmptyString, defaultValue
        );
    }

    public static Float scanUntilParsedPositiveFloat(
        Scanner scanner,
        Writer writer,
        String inputString,
        boolean allowEmptyString,
        Float defaultValue
    ) throws IOException {
        return Utils.scanUntilParsed(
            (string) -> {
                try {
                    Float result = Float.parseFloat(string);
                    if (result <= 0) {
                        throw new ParsingException();
                    }
                    return result;
                } catch (NumberFormatException err) {
                    throw new ParsingException();
                }
            }, scanner, writer, inputString, "Number greater than 0 with a floating point requried!", allowEmptyString, defaultValue
        );
    }

    public static Coordinates scanUntilParsedCoordinates(
        Scanner scanner,
        Writer writer,
        String inputString
    ) throws IOException {
        print(writer, inputString);
        int x = Utils.scanUntilParsed(
            (string) -> {
                try {
                    Integer result = Integer.parseInt(string);
                    if (result > 156) {
                        throw new ParsingException();
                    }
                    return result;
                } catch (NumberFormatException err) {
                    throw new ParsingException();
                }
            }, scanner, writer, "X coordinates: ", "Integer lower than 157 requried!", false, 0);
        Long y = Utils.scanUntilParsedLong(scanner, writer, "Y coordinates: ", false, null);

        return new Coordinates(x, y);
    }

    public static VehicleType scanUntilParsedVehicleType(
        Scanner scanner,
        Writer writer,
        String inputString,
        boolean allowEmptyString,
        VehicleType defaultValue
    ) throws IOException {
        Stream<String> vehicleTypesStream = Stream.of(VehicleType.values()).map(t -> t.name());
        String errorMessage = "VehicleType must be one of the following: " + vehicleTypesStream.collect(Collectors.joining(", "));
        VehicleType type = Utils.scanUntilParsed(
            VehicleType::parse,
            scanner, writer, inputString, errorMessage, allowEmptyString, defaultValue
        );
        return type;
    }

    public static FuelType scanUntilParsedFuelType(
        Scanner scanner,
        Writer writer,
        String inputString,
        boolean allowEmptyString,
        FuelType defaultValue
    ) throws IOException {
        Stream<String> fuelTypesStream = Stream.of(FuelType.values()).map(t -> t.name());
        String errorMessage = "FuelType must be one of the following: " + fuelTypesStream.collect(Collectors.joining(", "));
        FuelType type = Utils.scanUntilParsed(
            FuelType::parse,
            scanner, writer, inputString, errorMessage, allowEmptyString, defaultValue
        );
        return type;
    }
}
