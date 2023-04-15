package ru.ifmo.app.local.lib;

import java.io.File;
import java.util.Scanner;
import ru.ifmo.app.shared.Vehicles;
import ru.ifmo.app.shared.utils.DeprecatedCommandRegistery;

/**
 * A record, which contains all of the data needed by the {@link DeprecatedCommand commands}
 * <p>
 * Was implemented so that if the signature changes in some way (like adding an additional collection),
 * it wouldn't be neccesary to go through each class which implements {@link DeprecatedCommand} and change
 * the {@link DeprecatedCommand#execute execute} method.
 * </p>
 */
public record DeprecatedCommandContext(
    String[] arguments,
    Vehicles vehicles,
    File vehiclesFile,
    Scanner scanner,
    DeprecatedCommandRegistery commandRegistery,
    int scriptExecutionDepth
) {
    public boolean executedByScript() {
        return this.scriptExecutionDepth > 0;
    }
}
