package ru.ifmo.app.lib;

/**
 * Enumeration containing all of the xml element/attribute tags, which are used to store the
 * collection.
 */
public enum VehiclesXmlTag {
  Vehicles("vehicles"), Vehicle("vehicle"), IdAttr("id"), CreationDateAttr("creation-date"), Name(
      "name"), Coordinates("coordinates"), CoordinatesXAttr("x"), CoordinatesYAttr(
          "y"), EnginePower("engine-power"), VehicleType("vehicle-type"), FuelType("fuel-type");

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
