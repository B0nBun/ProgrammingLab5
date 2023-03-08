package ru.ifmo.app.lib.commands;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.CommandExecutor;
import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.exceptions.MaximumScriptExecutionDepthException;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to execute a script. Accepts a path to a file as an argument.
 *
 * <p>
 * A new CommandExecutor is created with the same arguments, except the scanner, instead of scanning
 * the stdin replace with the scanner of the file, path to which is passed in the arguments
 */
public class ExecuteScriptCommand implements Command {

  @Override
  public void execute(CommandContext context)
      throws InvalidArgumentException, InvalidNumberOfArgumentsException, ExitProgramException,
      MaximumScriptExecutionDepthException {
    int maximumScriptExecutionDepth = 100;
    if (context.scriptExecutionDepth() >= maximumScriptExecutionDepth) {
      throw new MaximumScriptExecutionDepthException(maximumScriptExecutionDepth);
    }

    if (context.arguments().length < 1)
      throw new InvalidNumberOfArgumentsException(1, context.arguments().length);

    String scriptFilepath = context.arguments()[0];

    try (Scanner fileScanner = new Scanner(new FileInputStream(scriptFilepath))) {
      var commandExecutor = new CommandExecutor(fileScanner, context.vehicles(),
          context.vehiclesFile(), context.scriptExecutionDepth() + 1);

      while (fileScanner.hasNextLine()) {
        String commandString = fileScanner.nextLine();
        commandExecutor.executeCommandString(commandString);
      }
    } catch (FileNotFoundException | SecurityException err) {
      throw new InvalidArgumentException(Messages.get("Help.Command.Arg.Filepath"),
          err.getMessage());
    }
  }

  @Override
  public String[] helpArguments() {
    return new String[] {Messages.get("Help.Command.Arg.Filepath")};
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.ExecuteScript");
  }
}
