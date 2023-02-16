package lib.exceptions;

public class InvalidNumberOfArgumentsException extends Exception {
    public InvalidNumberOfArgumentsException(int requiredNumber, int providedNumber) {
        super("Command requires " + requiredNumber + " number of arguments, but " + providedNumber + " were provided");
    }
}
