package ru.ifmo.app.shared.utils.fieldschema;

import java.util.List;
import java.util.Scanner;
import ru.ifmo.app.local.App;
import ru.ifmo.app.local.lib.exceptions.ParsingException;
import ru.ifmo.app.local.lib.exceptions.ValidationException;
import ru.ifmo.app.shared.Utils.Validator;
import ru.ifmo.app.shared.utils.Messages;

public interface FieldSchema<T, Self extends FieldSchema<T, Self>> extends Validator<T> {
    public T parse(String input) throws ParsingException;

    public List<Validator<T>> validators();

    public Self refine(Validator<T> validator);

    public default T validate(T value) throws ValidationException {
        try {
            for (var validator : this.validators()) {
                validator.validate(value);
            }
            return value;
        } catch (NullPointerException err) {
            return null;
        }
    }

    public default T fromString(String input)
        throws ValidationException, ParsingException {
        try {
            T parsed = this.parse(input);
            return this.validate(parsed);
        } catch (NumberFormatException err) {
            throw new ParsingException(err.getMessage());
        }
    }

    public default T prompt(String promptMessage, Scanner scanner, boolean inputLog)
        throws ValidationException, ParsingException {
        App.logger.info(promptMessage + ":");
        String input = scanner.nextLine();
        if (inputLog) App.logger.info(input);
        return this.fromString(input);
    }

    public default T promptUntilValid(
        String promptMessage,
        Scanner scanner,
        String parsingErrorMessage,
        boolean inputLog
    ) {
        while (true) {
            try {
                var scannedValue = this.prompt(promptMessage, scanner, inputLog);
                return scannedValue;
            } catch (ParsingException err) {
                App.logger.error(parsingErrorMessage);
            } catch (ValidationException err) {
                App.logger.error(err.getMessage());
            }
        }
    }

    public default Self nonnull() {
        return this.refine(
                Validator.from(
                    value -> value != null,
                    Messages.get("FieldSchema.Required")
                )
            );
    }

    public default Self notequals(T neqvalue) {
        return this.refine(
                Validator.from(
                    value -> !value.equals(neqvalue),
                    Messages.get("FieldSchema.NotEquals", neqvalue)
                )
            );
    }

    public default Self mustequal(T eqvalue) {
        return this.refine(
                Validator.from(
                    value -> value.equals(eqvalue),
                    Messages.get("FieldSchema.MustEqual", eqvalue)
                )
            );
    }

    public static FieldSchemaLocalDate localdate() {
        return new FieldSchemaLocalDate();
    }

    public static <TEnum extends Enum<TEnum>> FieldSchemaEnum<TEnum> enumeration(
        Class<TEnum> enumClass
    ) {
        return new FieldSchemaEnum<TEnum>(enumClass);
    }

    public static FieldSchemaString str() {
        return new FieldSchemaString();
    }

    public static FieldSchemaNum<Integer> integer() {
        return new FieldSchemaNum<>(Integer::parseInt);
    }

    public static FieldSchemaNum<Long> longint() {
        return new FieldSchemaNum<>(Long::parseLong);
    }

    public static FieldSchemaNum<Float> floating() {
        return new FieldSchemaNum<>(Float::parseFloat);
    }

    public static FieldSchemaNum<Double> doublef() {
        return new FieldSchemaNum<>(Double::parseDouble);
    }
}
