package ru.ifmo.app.lib.entities;

import java.util.Optional;

public record Coordinates(
    Long x,
    Integer y
) {
    public static class validate {
        private validate() {}

        public static Optional<String> x(Long x) {
            if (x == null) return Optional.of("'x' coordinate can't be empty");
            return Optional.empty();
        }

        public static Optional<String> y(Integer y) {
            if (y == null) return Optional.of("'y' coordinate can't be empty");
            if (y > -738) return Optional.of("'y' coordinate can't be greater than -738");
            return Optional.empty();
        }
    }
}
