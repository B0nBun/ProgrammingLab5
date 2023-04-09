package ru.ifmo.app.shared;

/**
 * Enumeration containing all of the xml element/attribute tags, which are used to store the
 * collection.
 */
public enum VehiclesXmlTag {
  VEHICLES("vehicles"), VEHICLE("vehicle"), ID_ATTR("id"), CREATION_DATE_ATTR(
      "creation-date"), NAME("name"), COORDINATES("coordinates"), COORDINATES_X_ATTR(
          "x"), COORDINATES_Y_ATTR("y"), ENGINE_POWER(
              "engine-power"), VEHICLE_TYPE("vehicle-type"), FUEL_TYPE("fuel-type");

  private final String tagName;

  /**
   * Constructor, which takes a tagName which then will be used to search for elements/attributes.
   *
   * @param tagName
   */
  VehiclesXmlTag(final String tagName) {
    this.tagName = tagName;
  }

  @Override
  public String toString() {
    return this.tagName;
  }
}
