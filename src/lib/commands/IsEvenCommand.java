package lib.commands;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import lib.Command;
import lib.Vehicles;
import lib.exceptions.CommandNotFoundException;
import lib.exceptions.InvalidArgumentException;

public class IsEvenCommand implements Command {
    @Override
    public void execute(
        String[] arguments,
        Vehicles vehicles,
        Scanner scanner,
        Writer writer
    ) throws CommandNotFoundException, InvalidArgumentException, IOException {
        Integer num = null;
        if (arguments.length == 0) {
            print(writer, "Input a number: ");
            while (num == null) {
                try {
                    num = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException err) {
                    print(writer, "Please input an integer\n");
                }
            }
        } else {
            String firstArg = arguments[0];
            try {
                num = Integer.parseInt(firstArg);
            } catch (NumberFormatException err) {
                throw new InvalidArgumentException("Expected an integer, found '" + firstArg + "'");
            }
        }

        if (num % 2 == 0) {
            print(writer, num + " is an even number\n");
        } else {
            print(writer, num + " is an odd number\n");
        }
    }

    private void print(Writer writer, String str) throws IOException {
        writer.write(str);
        writer.flush();
    }
}
