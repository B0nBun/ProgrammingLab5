package lib;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import lib.exceptions.CommandNotFoundException;
import lib.exceptions.InvalidArgumentException;

public interface Command {
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer
    ) throws CommandNotFoundException, InvalidArgumentException, IOException;
}
