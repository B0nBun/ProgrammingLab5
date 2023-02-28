package ru.ifmo.app.lib.utils;

import java.io.Writer;
import java.util.Scanner;
import java.util.function.Function;

import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Utils.NumberParser;
import ru.ifmo.app.lib.Utils.Validator;
import ru.ifmo.app.lib.entities.FuelType;
import ru.ifmo.app.lib.entities.Vehicle;
import ru.ifmo.app.lib.entities.VehicleType;
import ru.ifmo.app.lib.exceptions.ParsingException;

public record ValidatedScanner(
    Scanner scanner,
    Writer writer
) {
    public String string(String inputString, Validator<String> validator) {
        return Utils.scanUntilValid(line -> line, validator, scanner, writer, inputString, Exception::getMessage);
    }

    public <T> T number(NumberParser<T> numberParser, Validator<T> validator, String inputString, Function<ParsingException, String> parsingErrorMessage) {
        return Utils.scanUntilValid(
            string -> {
                try {
                    return numberParser.parse(string);
                } catch (NumberFormatException err) {
                    throw new ParsingException(err.getMessage());
                }
            },
            validator, scanner, writer, inputString, parsingErrorMessage
        );
    }

    public VehicleType vehicleType(String inputString) {
        VehicleType type = Utils.scanUntilValid(
            VehicleType::parse,
            Vehicle.validate::vehicleType,
            scanner, writer, inputString,
            Exception::getMessage
        );
        return type;
    }

    public FuelType fuelType(String inputString) {
        FuelType type = Utils.scanUntilValid(
            FuelType::parse,
            Vehicle.validate::fuelType,
            scanner, writer, inputString,
            Exception::getMessage
        );
        return type;
    }
}
