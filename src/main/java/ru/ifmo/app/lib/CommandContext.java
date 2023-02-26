package ru.ifmo.app.lib;

import java.io.File;
import java.io.Writer;
import java.util.Scanner;

import ru.ifmo.app.lib.utils.CommandRegistery;

public record CommandContext(
    String[] arguments,
    Vehicles vehicles,
    File vehiclesFile,
    Scanner scanner,
    Writer writer,
    CommandRegistery commandRegistery
) {}
