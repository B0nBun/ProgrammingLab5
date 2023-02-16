package lib.entities;

import lib.exceptions.ParsingException;

public enum FuelType {
    GASOLINE,
    KEROSENE,
    ALCOHOL;

    public static FuelType parse(String string) throws ParsingException {
        try {
            return FuelType.valueOf(string);
        } catch (IllegalArgumentException err) {
            throw new ParsingException();
        }
    }
}
