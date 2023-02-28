package ru.ifmo.app.lib.entities;

import java.util.Arrays;
import java.util.stream.Stream;

import ru.ifmo.app.lib.exceptions.ParsingException;
import ru.ifmo.app.lib.utils.Messages;

public enum VehicleType {
    DRONE,
    BOAT,
    BICYCLE,
    CHOPPER;

    public static String showIndexedList(String joiner) {
        var names = Stream.of(VehicleType.values()).map(t -> t.name()).toList();
        String result = "";
        for (int i = 0; i < names.size(); i ++) {
            result += (i + 1) + ". " + names.get(i) + (i == names.size() - 1 ? "" : joiner);
        }
        return result;
    }
    
    public static VehicleType parse(String string) throws ParsingException {
        String errorMessage = Messages.get("Error.Validation.MustBeOneOfTheFollowing", Messages.get("Vehicle.VehicleType"), VehicleType.showIndexedList(", "));
        try {
            Integer index = Integer.parseUnsignedInt(string);
            try {
                return VehicleType.values()[index - 1];
            } catch (ArrayIndexOutOfBoundsException _err) {
                throw new ParsingException(errorMessage);
            }
        } catch (NumberFormatException _err) {
            var value = Arrays
                .stream(VehicleType.values())
                .filter(e -> e.name().equalsIgnoreCase(string))
                .findAny()
                .orElse(null);
                
            if (value == null)
                throw new ParsingException(errorMessage);
            return value;
        }
    }
}