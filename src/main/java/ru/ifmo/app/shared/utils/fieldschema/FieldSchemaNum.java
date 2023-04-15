package ru.ifmo.app.shared.utils.fieldschema;

import java.util.ArrayList;
import java.util.List;
import ru.ifmo.app.local.lib.exceptions.ParsingException;
import ru.ifmo.app.shared.Utils.NumberParser;
import ru.ifmo.app.shared.Utils.Validator;

public class FieldSchemaNum<N extends Comparable<N>>
    implements FieldSchemaComparable<N, FieldSchemaNum<N>> {

    private List<Validator<N>> validators;
    private NumberParser<N> parser;

    private FieldSchemaNum(
        NumberParser<N> numberParser,
        List<Validator<N>> initValidators
    ) {
        this.parser = numberParser;
        this.validators = initValidators;
    }

    FieldSchemaNum(NumberParser<N> numberParser) {
        this(numberParser, new ArrayList<>());
    }

    public N parse(String input) throws ParsingException {
        if (input == null) return null;

        try {
            N parsed = parser.parse(input);
            return parsed;
        } catch (NumberFormatException err) {
            throw new ParsingException(err.getMessage());
        }
    }

    public List<Validator<N>> validators() {
        return this.validators;
    }

    public FieldSchemaNum<N> refine(Validator<N> validator) {
        var newValidators = new ArrayList<>(this.validators);
        newValidators.add(validator);
        return new FieldSchemaNum<>(parser, newValidators);
    }
}
