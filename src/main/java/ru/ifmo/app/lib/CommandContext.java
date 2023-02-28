package ru.ifmo.app.lib;

import java.io.File;
import java.util.Scanner;

import ru.ifmo.app.lib.utils.CommandRegistery;

public record CommandContext(
    String[] arguments,
    Vehicles vehicles,
    File vehiclesFile,
    Scanner scanner,
    CommandRegistery commandRegistery
) {}
