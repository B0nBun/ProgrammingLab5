package ru.ifmo.app.shared.utils.fieldschema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.ifmo.app.local.lib.exceptions.ParsingException;
import ru.ifmo.app.shared.Utils.Validator;
import ru.ifmo.app.shared.utils.Messages;

public class FieldSchemaEnum<TEnum extends Enum<TEnum>>
    implements FieldSchemaComparable<TEnum, FieldSchemaEnum<TEnum>> {

    private List<Validator<TEnum>> validators;
    private boolean allowIndex;
    private Class<TEnum> enumClass;

    private FieldSchemaEnum(
        List<Validator<TEnum>> initValidators,
        Class<TEnum> enumClass,
        boolean allowIndex
    ) {
        this.validators = initValidators;
        this.enumClass = enumClass;
        this.allowIndex = allowIndex;
    }

    FieldSchemaEnum(Class<TEnum> enumClass) {
        this(new ArrayList<>(), enumClass, true);
    }

    public FieldSchemaEnum<TEnum> disallowIndex() {
        return new FieldSchemaEnum<>(this.validators, this.enumClass, false);
    }

    public TEnum parse(String input) throws ParsingException {
        if (input == null || input.equals("")) return null;

        var value = Arrays
            .stream(enumClass.getEnumConstants())
            .filter(e -> e.name().equalsIgnoreCase(input))
            .findAny()
            .orElse(null);

        if (value == null && this.allowIndex) {
            try {
                Integer index = Integer.parseUnsignedInt(input);
                return enumClass.getEnumConstants()[index - 1];
            } catch (ArrayIndexOutOfBoundsException _err) {
                throw new ParsingException(
                    Messages.get(
                        "FieldSchemaEnum.OutOfBounds",
                        enumClass.getEnumConstants().length
                    )
                );
            } catch (NumberFormatException err) {
                throw new ParsingException(err.getMessage());
            }
        }

        if (value == null) {
            throw new ParsingException(
                Messages.get("FieldSchemaEnum.InvalidValue", input)
            );
        }

        return value;
    }

    public List<Validator<TEnum>> validators() {
        return this.validators;
    }

    public FieldSchemaEnum<TEnum> refine(Validator<TEnum> validator) {
        var newValidators = new ArrayList<>(this.validators);
        newValidators.add(validator);
        return new FieldSchemaEnum<>(newValidators, this.enumClass, this.allowIndex);
    }
}
