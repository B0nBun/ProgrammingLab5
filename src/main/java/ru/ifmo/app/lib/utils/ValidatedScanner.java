package ru.ifmo.app.lib.utils;

import java.util.Scanner;
import java.util.function.Function;

import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Utils.NumberParser;
import ru.ifmo.app.lib.Utils.Validator;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.entities.Vehicle;
import ru.ifmo.app.lib.entities.VehicleType;
import ru.ifmo.app.lib.exceptions.ParsingException;

/**
 * A record, which has the methods for easier parsing and validation of scanned inputs.
 * Given the scanner, it can use it's {@link Scanner#nextLine} method to scan the input
 * and then, using {@link Utils#scanUntilValid} method parse and validate the input.
 * 
 */
public record ValidatedScanner(
    Scanner scanner,
    boolean logScanned
) {
    /**
     * Scans the lines from input, until scanned string passes the validation function.
     * 
     * @param inputString A string logged before each line scan. Can serve as a prompt message.
     * @param validator A validator to which scanned strings are passed
     * @return First string from scanner, which passed validation 
     */
    public String string(String inputString, Validator<String> validator) {
        return Utils.scanUntilValid(line -> line, validator, scanner, inputString, Exception::getMessage, this.logScanned);
    }

    /**
     * Scans the lines from input, until scanned string is parsed as a number with {@link NumberParser} and validated.
     * 
     * @param <T> A type parsed from the string
     * @param numberParser A {@link NumberParser} responsible for parsing the input
     * @param validator A validator of the parsed value
     * @param inputString A string logged before each line scan. Can serve as a promp message
     * @param parsingErrorMessage A method which converts ParsingException to the string, which the will be logged
     * @return Parsed and validated value of T type
     */
    public <T> T number(NumberParser<T> numberParser, Validator<T> validator, String inputString, Function<ParsingException, String> parsingErrorMessage) {
        return Utils.scanUntilValid(
            string -> {
                try {
                    return numberParser.parse(string);
                } catch (NumberFormatException err) {
                    throw new ParsingException(err.getMessage());
                }
            },
            validator, scanner, inputString, parsingErrorMessage, this.logScanned
        );
    }

    /**
     * Scans the lines from input, until scanned string is parsed as a {@link VehicleType}
     * 
     * @param inputString A string logged before each line scan. Can serve as a promp message
     * @return Parsed {@link VehicleType}
     */
    public VehicleType vehicleType(String inputString) {
        VehicleType type = Utils.scanUntilValid(
            VehicleType::parse,
            Vehicle.validate::vehicleType,
            scanner,
            inputString,
            Exception::getMessage,
            this.logScanned
        );
        return type;
    }

    /**
     * Scans the lines from input, until scanned string is parsed as a {@link FuelType}
     * 
     * @param inputString A string logged before each line scan. Can serve as a promp message
     * @return Parsed {@link FuelType}
     */
    public FuelType fuelType(String inputString) {
        FuelType type = Utils.scanUntilValid(
            FuelType::parse,
            Vehicle.validate::fuelType,
            scanner,
            inputString,
            Exception::getMessage,
            this.logScanned
        );
        return type;
    }
}
