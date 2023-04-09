package ru.ifmo.app.local.lib.commands;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import ru.ifmo.app.local.App;
import ru.ifmo.app.local.lib.DeprecatedCommand;
import ru.ifmo.app.local.lib.DeprecatedCommandContext;
import ru.ifmo.app.local.lib.DeprecatedCommandExecutor;
import ru.ifmo.app.local.lib.exceptions.ExitProgramException;
import ru.ifmo.app.local.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.local.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.local.lib.exceptions.MaximumScriptExecutionDepthException;
import ru.ifmo.app.shared.Utils;
import ru.ifmo.app.shared.utils.Messages;

/**
 * Command used to execute a script. Accepts a path to a file as an argument.
 *
 * <p>
 * A new CommandExecutor is created with the same arguments, except the scanner, instead of scanning
 * the stdin replace with the scanner of the file, path to which is passed in the arguments
 */
public class ExecuteScriptCommand implements DeprecatedCommand {

  @Override
  public void execute(DeprecatedCommandContext context)
      throws InvalidArgumentException, InvalidNumberOfArgumentsException, ExitProgramException,
      MaximumScriptExecutionDepthException {
    int maximumScriptExecutionDepth = 100;
    if (context.scriptExecutionDepth() >= maximumScriptExecutionDepth) {
      throw new MaximumScriptExecutionDepthException(maximumScriptExecutionDepth);
    }

    if (context.arguments().length < 1)
      throw new InvalidNumberOfArgumentsException(1, context.arguments().length);

    String scriptFilepath = Utils.expandPath(context.arguments()[0]);

    try (Scanner fileScanner = new Scanner(new FileInputStream(scriptFilepath))) {
      var commandExecutor = new DeprecatedCommandExecutor(fileScanner, context.vehicles(),
          context.vehiclesFile(), context.scriptExecutionDepth() + 1);

      while (fileScanner.hasNextLine()) {
        String commandString = fileScanner.nextLine();
        App.logger.info(commandString);
        commandExecutor.executeCommandString(commandString);
      }
    } catch (FileNotFoundException | SecurityException err) {
      throw new InvalidArgumentException(Messages.get("Help.Command.Arg.Filepath"),
          err.getMessage());
    } catch (NoSuchElementException err) {
      App.logger.error(Messages.get("Error.NoSuchElement.ScriptEnded"));
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
