package ru.ifmo.app.lib;

public enum VehiclesXmlTag {
    Vehicles("vehicles"),
    Vehicle("vehicle"),
    IdAttr("id"),
    CreationDateAttr("creation-date"),
    Name("name"),
    Coordinates("coordinates"),
    CoordinatesXAttr("x"),
    CoordinatesYAttr("y"),
    EnginePower("engine-power"),
    VehicleType("vehicle-type"),
    FuelType("fuel-type");
    

    private final String tagName;

    VehiclesXmlTag(final String tagName) {
        this.tagName = tagName;
    }

    @Override
    public String toString() {
        return this.tagName;
    }
}
