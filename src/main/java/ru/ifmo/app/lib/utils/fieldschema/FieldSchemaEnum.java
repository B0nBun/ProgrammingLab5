package ru.ifmo.app.lib.utils.fieldschema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.ifmo.app.lib.Utils.Validator;
import ru.ifmo.app.lib.exceptions.ParsingException;

public class FieldSchemaEnum<TEnum extends Enum<TEnum>>
    implements FieldSchemaOrd<TEnum, FieldSchemaEnum<TEnum>> {
  private List<Validator<TEnum>> validators;
  private boolean allowIndex;
  private Class<TEnum> enumClass;

  private FieldSchemaEnum(List<Validator<TEnum>> initValidators, Class<TEnum> enumClass,
      boolean allowIndex) {
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
    var value = Arrays.stream(enumClass.getEnumConstants())
        .filter(e -> e.name().equalsIgnoreCase(input)).findAny().orElse(null);

    if (value == null && this.allowIndex) {
      try {
        Integer index = Integer.parseUnsignedInt(input);
        return enumClass.getEnumConstants()[index - 1];
      } catch (ArrayIndexOutOfBoundsException | NumberFormatException _err) {
        // TODO: Log the error
        throw new ParsingException("");
      }
    }

    if (value == null) {
      // TODO: Log the error
      throw new ParsingException("");
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
