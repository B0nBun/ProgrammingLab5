package ru.ifmo.app.lib;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import ru.ifmo.app.lib.exceptions.ParsingException;
import ru.ifmo.app.lib.exceptions.ValidationException;
import ru.ifmo.app.lib.utils.Messages;

/** A class that serves as a namespace for utility methods and functional interfaces */
public class Utils {

  public static <T> ByteBuffer objectToBuffer(T object) throws IOException {
    try (var byteOut = new ByteArrayOutputStream();
        var objectStream = new ObjectOutputStream(byteOut);) {
      objectStream.writeObject(object);
      byte[] objectBytes = byteOut.toByteArray();

      int objectSize = objectBytes.length;
      var buffer = ByteBuffer.allocate(Integer.BYTES + objectSize);
      buffer.putInt(objectSize);
      buffer.put(objectBytes);
      buffer.flip();

      return buffer;
    }
  }

  public static <T> T objectFromChannel(ReadableByteChannel channel, Function<Object, T> converter)
      throws IOException, ClassNotFoundException {
    var objectSizeBuffer = ByteBuffer.allocate(Integer.BYTES);
    channel.read(objectSizeBuffer);
    objectSizeBuffer.position(0);
    int objectSize = objectSizeBuffer.getInt();

    var objectBuffer = ByteBuffer.allocate(Integer.BYTES + objectSize);
    channel.read(objectBuffer);
    objectBuffer.position(Integer.BYTES); // Skipping the part with the object size
    try (var objectInputStream =
        new ObjectInputStream(new ByteArrayInputStream(objectBuffer.array()));) {
      T got = converter.apply(objectInputStream.readObject());
      return got;
    }
  }

  /**
   * A function, which expands the given path by reading the output from the subprocess command
   * `bash -c 'echo {path}'`
   * <p>
   * Source: https://stackoverflow.com/questions/7163364/how-to-handle-in-file-paths
   * </p>
   * <i> Replaced `ls` with `echo` command, because it didn't work with non-existant files </i>
   * 
   * @param path
   * @return
   */
  public static String expandPath(String path) {
    try {
      String command = "echo " + path;
      Process shellExec = Runtime.getRuntime().exec(new String[] {"bash", "-c", command});

      BufferedReader reader = new BufferedReader(new InputStreamReader(shellExec.getInputStream()));
      String expandedPath = reader.readLine();

      // Only return a new value if expansion worked.
      // We're reading from stdin. If there was a problem, it was written
      // to stderr and our result will be null.
      if (expandedPath != null) {
        path = expandedPath;
      }
    } catch (java.io.IOException ex) {
      // Just consider it unexpandable and return original path.
    }

    return path;
  }

  public static Optional<String> validateFilename(String filename) {
    if (filename.contains(" ")) {
      return Optional.of(Messages.get("Error.Validation.FilenameContainsSpaces", filename));
    }
    String illegalCharacters = "#%&{}\\<>*?/ $!'\":@+`|=";
    boolean illegalCharacterFound =
        illegalCharacters.chars().map(c -> filename.indexOf(c)).anyMatch(i -> i != -1);
    if (illegalCharacterFound) {
      return Optional.of(
          Messages.get("Error.Validation.FilenameIllegalCharacter", illegalCharacters, filename));
    }

    return Optional.empty();
  }

  /**
   * Functional interface which implements the {@code validate} method. See
   * {@link DeprecatedValidator#validate} for more detailed description.
   */
  @FunctionalInterface
  public static interface DeprecatedValidator<T> {
    /**
     * Function, which validates that the value of type {@code T} is correct.
     *
     * <p>
     * <i>(I don't remember why I did it with Optional and not with a checked exception)</i>
     *
     * @param value
     * @return An {@link Optional} of a String, which serves as an error signifier. So if returned
     *         optional is empty, then the value is considered validated. Otherwise, the String in
     *         the optional is considered to be an error message.
     */
    Optional<String> validate(T value);
  }

  /**
   * Functional interface which implements the {@code validate} method. See
   * {@link Validator#validate} for more detailed description
   */
  @FunctionalInterface
  public static interface Validator<T> {
    /**
     * Function, which validates that the value of type {@code T} is correct
     * 
     * @param value Value to validate
     * @return The same value if it is valid, otherwise throws
     * @throws ValidationException Thrown if the validation failed
     */
    T validate(T value) throws ValidationException;

    public static <T> Validator<T> from(Predicate<T> predicate, Function<T, String> messageGetter) {
      return value -> {
        boolean valid = predicate.test(value);
        if (valid)
          return value;
        throw new ValidationException(messageGetter.apply(value));
      };
    }

    public static <T> Validator<T> from(Predicate<T> predicate, String failMessage) {
      return Validator.from(predicate, __ -> failMessage);
    }
  }

  /**
   * Functional interface which implements the {@code parse} method. See {@link NumberParser#parse}
   * for more detailed description.
   */
  @FunctionalInterface
  public static interface NumberParser<N> {
    /**
     * Method, which parses some kind of the number value from provided String. (It actually can be
     * any type, but {@link NumberFormatException} is what signifies the failed parsing)
     *
     * @param string A string from which the value should be parsed
     * @return A parsed value
     * @throws NumberFormatException This exception is thrown if the parsing is failed
     */
    N parse(String string) throws NumberFormatException;
  }

  /**
   * Functional interface which implements the {@code tryToParse} method. See
   * {@link ParsingFunction#tryToParse} for more detailed description.
   */
  @FunctionalInterface
  public static interface ParsingFunction<T> {
    /**
     * Method, which parses value of type {@code T} from provided String.
     *
     * @param string A string from which the value should be parsed
     * @return A parsed value
     * @throws ParsingException This exception is thrown if the parsing is failed
     */
    T tryToParse(String string) throws ParsingException;
  }
}
