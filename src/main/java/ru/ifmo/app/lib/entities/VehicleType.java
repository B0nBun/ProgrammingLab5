package ru.ifmo.app.lib.entities;

import java.util.Arrays;
import java.util.stream.Stream;
import ru.ifmo.app.lib.exceptions.ParsingException;
import ru.ifmo.app.lib.utils.Messages;

/** Enumeration representing the type of the {@link Vehicle} */
public enum VehicleType {
  DRONE, BOAT, BICYCLE, CHOPPER;

  /**
   * Get joined strings of vehicle types.
   *
   * @param joiner A string with which type-strings will be joined
   * @return A string with all of the vehicle types, seperated by a joiner (e.g. "BOAT, DRONE, ...")
   */
  public static String showIndexedList(String joiner) {
    var names = Stream.of(VehicleType.values()).map(t -> t.name()).toList();
    String result = "";
    for (int i = 0; i < names.size(); i++) {
      result += (i + 1) + ". " + names.get(i) + (i == names.size() - 1 ? "" : joiner);
    }
    return result;
  }

  /**
   * Try to parse the VehicleType from the string.
   *
   * <p>
   * Given string can be either the enumeration value, regardless of the characters' registery (e.g.
   * "DrOne"), or it can be enumeration's value's index, starting with 1 (e.g. "1" will be
   * interpreted as {@link VehicleType#DRONE})
   *
   * @param string A string to be parsed
   * @return Vehicle type parsed from the given string
   * @throws ParsingException Thrown if the parsing failed
   */
  public static VehicleType parse(String string) throws ParsingException {
    String errorMessage = Messages.get("Error.Validation.MustBeOneOfTheFollowing",
        Messages.get("Vehicle.VehicleType"), VehicleType.showIndexedList(", "));
    try {
      Integer index = Integer.parseUnsignedInt(string);
      try {
        return VehicleType.values()[index - 1];
      } catch (ArrayIndexOutOfBoundsException _err) {
        throw new ParsingException(errorMessage);
      }
    } catch (NumberFormatException _err) {
      var value = Arrays.stream(VehicleType.values()).filter(e -> e.name().equalsIgnoreCase(string))
          .findAny().orElse(null);

      if (value == null)
        throw new ParsingException(errorMessage);
      return value;
    }
  }
}
