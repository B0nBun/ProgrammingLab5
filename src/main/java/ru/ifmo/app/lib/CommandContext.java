package ru.ifmo.app.lib;

import java.io.File;
import java.util.Scanner;

import ru.ifmo.app.lib.utils.CommandRegistery;

/**
 * A record, which contains all of the data needed by the {@link Command commands}
 * <p>
 * Was implemented so that if the signature changes in some way (like adding an additional collection),
 * it wouldn't be neccesary to go through each class which implements {@link Command} and change
 * the {@link Command#execute execute} method.
 * </p>
 */
public record CommandContext(
    String[] arguments,
    Vehicles vehicles,
    File vehiclesFile,
    Scanner scanner,
    CommandRegistery commandRegistery
) {}
