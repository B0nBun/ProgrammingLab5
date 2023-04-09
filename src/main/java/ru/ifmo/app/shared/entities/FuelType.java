package ru.ifmo.app.shared.entities;

import java.util.Arrays;
import java.util.stream.Stream;
import ru.ifmo.app.local.lib.exceptions.ParsingException;
import ru.ifmo.app.shared.utils.Messages;

/** Enumeration representing the fuel type of the {@link Vehicle} */
public enum FuelType {
  GASOLINE, KEROSENE, ALCOHOL;

  /**
   * Get joined strings of fuel types.
   *
   * @param joiner A string with which type-strings will be joined
   * @return A string with all of the fuel types, seperated by a joiner (e.g. "GASOLINE, KEROSENE,
   *         ...")
   */
  public static String showIndexedList(String joiner) {
    var names = Stream.of(FuelType.values()).map(t -> t.name()).toList();
    String result = "";
    for (int i = 0; i < names.size(); i++) {
      result += (i + 1) + ". " + names.get(i) + (i == names.size() - 1 ? "" : joiner);
    }
    return result;
  }

  /**
   * Try to parse the FuelType from the string.
   *
   * <p>
   * Given string can be either the enumeration value, regardless of the characters' registery (e.g.
   * "GaSolIne"), or it can be enumeration's value's index, starting with 1 (e.g. "1" will be
   * interpreted as {@link FuelType#GASOLINE})
   *
   * @param string A string to be parsed
   * @return Fuel type parsed from the given string
   * @throws ParsingException Thrown if the parsing failed
   */
  public static FuelType parse(String string) throws ParsingException {
    String errorMessage = Messages.get("Error.Validation.MustBeOneOfTheFollowing",
        Messages.get("Vehicle.FuelType"), FuelType.showIndexedList(", "));
    try {
      Integer index = Integer.parseUnsignedInt(string);
      try {
        return FuelType.values()[index - 1];
      } catch (ArrayIndexOutOfBoundsException _err) {
        throw new ParsingException(errorMessage);
      }
    } catch (NumberFormatException _err) {
      var value = Arrays.stream(FuelType.values()).filter(e -> e.name().equalsIgnoreCase(string))
          .findAny().orElse(null);

      if (value == null)
        throw new ParsingException(errorMessage);
      return value;
    }
  }
}
