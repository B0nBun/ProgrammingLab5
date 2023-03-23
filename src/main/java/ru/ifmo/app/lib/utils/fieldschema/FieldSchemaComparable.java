package ru.ifmo.app.lib.utils.fieldschema;

import ru.ifmo.app.lib.Utils.Validator;

public interface FieldSchemaComparable<T extends Comparable<T>, Self extends FieldSchemaComparable<T, Self>>
    extends FieldSchema<T, Self> {
  default public Self max(T maxValue) {
    return this.refine(Validator.from(value -> value.compareTo(maxValue) <= 0,
        "value can not be greater than " + maxValue));
  }

  default public Self min(T minValue) {
    return this.refine(Validator.from(value -> value.compareTo(minValue) >= 0,
        "value can not be lesser than " + minValue));
  }

  default public Self clamped(T minValue, T maxValue) {
    return this.refine(
        Validator.from(value -> value.compareTo(minValue) >= 0 && value.compareTo(maxValue) <= 0,
            "value must be between " + minValue + " and " + maxValue));
  }
}
