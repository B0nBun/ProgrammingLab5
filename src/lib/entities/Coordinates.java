package lib.entities;

import lib.exceptions.InvalidArgumentException;

public record Coordinates(
    int x,
    Long y
) {

    public static Coordinates constructChecked(int x, Long y) throws InvalidArgumentException {
        if (x > 156)
            throw new InvalidArgumentException("x", "max 'x' value is 156");

        if (y == null)
            throw new InvalidArgumentException("y", "y is required");

        return new Coordinates(x, y);
    }
}
