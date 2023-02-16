package lib.entities;

import lib.exceptions.ParsingException;

public enum VehicleType {
    DRONE,
    BOAT,
    BICYCLE,
    CHOPPER;

    public static VehicleType parse(String string) throws ParsingException {
        try {
            return VehicleType.valueOf(string);
        } catch (IllegalArgumentException err) {
            throw new ParsingException();
        }
    }
}