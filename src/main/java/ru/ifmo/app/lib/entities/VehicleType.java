package ru.ifmo.app.lib.entities;

import java.util.stream.Stream;

import ru.ifmo.app.lib.exceptions.ParsingException;

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
        try {
            Integer index = Integer.parseUnsignedInt(string);
            try {
                return VehicleType.values()[index - 1];
            } catch (ArrayIndexOutOfBoundsException _err) {
                throw new ParsingException();
            }
        } catch (NumberFormatException _err) {
            try {
                return VehicleType.valueOf(string);
            } catch (IllegalArgumentException _err2) {
                throw new ParsingException();
            }
        }
    }
}