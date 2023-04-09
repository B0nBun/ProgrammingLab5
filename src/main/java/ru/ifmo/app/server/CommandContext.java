package ru.ifmo.app.server;

import java.io.PrintWriter;

import ru.ifmo.app.shared.CommandRegistery;
import ru.ifmo.app.shared.Vehicles;

public record CommandContext(
    CommandRegistery commandRegistery,
    PrintWriter outputWriter,
    Vehicles vehicles
) {}
