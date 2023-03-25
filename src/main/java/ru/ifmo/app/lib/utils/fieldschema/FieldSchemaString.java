package ru.ifmo.app.lib.utils.fieldschema;

import java.util.ArrayList;
import java.util.List;
import ru.ifmo.app.lib.Utils.Validator;
import ru.ifmo.app.lib.utils.Messages;

public class FieldSchemaString implements FieldSchemaComparable<String, FieldSchemaString> {
  private List<Validator<String>> validators;

  private FieldSchemaString(List<Validator<String>> initValidators) {
    this.validators = initValidators;
  }

  FieldSchemaString() {
    this(new ArrayList<>());
  }

  public FieldSchemaString nonempty() {
    return this
        .refine(Validator.from(s -> s.length() != 0, Messages.get("FieldSchemaString.NotEmpty")));
  }

  public String parse(String input) {
    return input;
  }

  public List<Validator<String>> validators() {
    return this.validators;
  }

  public FieldSchemaString refine(Validator<String> validator) {
    var newValidators = new ArrayList<>(this.validators);
    newValidators.add(validator);
    return new FieldSchemaString(newValidators);
  }
}
