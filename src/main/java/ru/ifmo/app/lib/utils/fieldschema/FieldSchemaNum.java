package ru.ifmo.app.lib.utils.fieldschema;

import java.util.ArrayList;
import java.util.List;
import ru.ifmo.app.lib.Utils.NumberParser;
import ru.ifmo.app.lib.Utils.Validator;
import ru.ifmo.app.lib.exceptions.ParsingException;

public class FieldSchemaNum<N extends Comparable<N>>
    implements FieldSchemaOrd<N, FieldSchemaNum<N>> {
  private List<Validator<N>> validators;
  private NumberParser<N> parser;

  private FieldSchemaNum(NumberParser<N> numberParser, List<Validator<N>> initValidators) {
    this.validators = initValidators;
  }

  FieldSchemaNum(NumberParser<N> numberParser) {
    this(numberParser, new ArrayList<>());
  }

  public N parse(String input) throws ParsingException {
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
