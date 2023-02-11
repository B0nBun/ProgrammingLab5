package lib.exceptions;

public class InvalidArgumentException extends Exception {
    public InvalidArgumentException(
        String message
    ) {
        super("Invalid argument:" + message);
    }
    
    public InvalidArgumentException(
        String argumentName,
        String message
    ) {
        super(
            "Invalid argument '" + argumentName + "': " + message
        );
    }
}
