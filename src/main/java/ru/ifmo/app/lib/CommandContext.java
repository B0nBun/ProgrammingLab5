package ru.ifmo.app.lib;

import java.io.Writer;
import java.util.Scanner;

import ru.ifmo.app.lib.Utils.CommandRegistery;

public record CommandContext(
    String[] arguments,
    Vehicles vehicles,
    Scanner scanner,
    Writer writer,
    CommandRegistery commandRegistery
) {}
