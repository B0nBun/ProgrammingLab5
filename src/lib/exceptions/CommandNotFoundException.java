package lib.exceptions;

public class CommandNotFoundException extends Exception {
    public CommandNotFoundException(String invalidCommand) {
        super("Command `"+invalidCommand+"` not found, type `help` to see the list of all commands");
    }
}
