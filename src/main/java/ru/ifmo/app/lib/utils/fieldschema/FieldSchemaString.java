package ru.ifmo.app.lib.utils.fieldschema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import ru.ifmo.app.lib.Utils.Validator;

public class FieldSchemaString implements FieldSchemaOrd<String, FieldSchemaString> {
  private List<Validator<String>> validators;

  private FieldSchemaString(List<Validator<String>> initValidators) {
    this.validators = initValidators;
  }

  FieldSchemaString() {
    this(new ArrayList<>());
  }

  public FieldSchemaString notcontains(String... strings) {
    return this.refine(Validator.from(
        value -> !Arrays.stream(strings).anyMatch(str -> value.contains(str)),
        "string can't contain the strings from list: "
            + Arrays.stream(strings).map(s -> "'" + s + "'").collect(Collectors.joining(", "))));
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
