package ru.ifmo.app.server;

import java.io.PrintWriter;

import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.shared.CommandRegistery;

public record CommandContext(
    CommandRegistery commandRegistery,
    PrintWriter outputWriter,
    Vehicles vehicles
) {}
