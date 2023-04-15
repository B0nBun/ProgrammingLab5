package ru.ifmo.app.shared.utils.fieldschema;

import ru.ifmo.app.shared.Utils.Validator;
import ru.ifmo.app.shared.utils.Messages;

public interface FieldSchemaComparable<
    T extends Comparable<T>, Self extends FieldSchemaComparable<T, Self>
>
    extends FieldSchema<T, Self> {
    public default Self max(T maxValue) {
        return this.refine(
                Validator.from(
                    value -> value.compareTo(maxValue) <= 0,
                    Messages.get("FieldSchemaComparable.Max", maxValue)
                )
            );
    }

    public default Self greaterThan(T minValue) {
        return this.refine(
                Validator.from(
                    value -> value.compareTo(minValue) > 0,
                    Messages.get("FieldSchemaComparable.GreaterThan", minValue)
                )
            );
    }

    public default Self min(T minValue) {
        return this.refine(
                Validator.from(
                    value -> value.compareTo(minValue) >= 0,
                    Messages.get("FieldSchemaComparable.Min", minValue)
                )
            );
    }

    public default Self clamped(T minValue, T maxValue) {
        return this.refine(
                Validator.from(
                    value ->
                        value.compareTo(minValue) >= 0 && value.compareTo(maxValue) <= 0,
                    Messages.get("FieldSchemaComparable.Clamped", minValue, maxValue)
                )
            );
    }
}
