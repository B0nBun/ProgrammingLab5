package ru.ifmo.app.shared.utils.fieldschema;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import ru.ifmo.app.local.lib.exceptions.ParsingException;
import ru.ifmo.app.shared.Utils.Validator;

public class FieldSchemaLocalDate
    implements FieldSchema<LocalDate, FieldSchemaLocalDate> {

    private List<Validator<LocalDate>> validators;

    private FieldSchemaLocalDate(List<Validator<LocalDate>> initValidators) {
        this.validators = initValidators;
    }

    FieldSchemaLocalDate() {
        this(new ArrayList<>());
    }

    public LocalDate parse(String input) throws ParsingException {
        if (input == null) return null;

        try {
            LocalDate parsed = LocalDate.parse(input);
            return parsed;
        } catch (DateTimeParseException err) {
            throw new ParsingException(err.getMessage());
        }
    }

    public List<Validator<LocalDate>> validators() {
        return this.validators;
    }

    public FieldSchemaLocalDate refine(Validator<LocalDate> validator) {
        var newValidators = new ArrayList<>(this.validators);
        newValidators.add(validator);
        return new FieldSchemaLocalDate(newValidators);
    }
}
