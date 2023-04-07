package ru.ifmo.app.lib;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import ru.ifmo.app.App;
import ru.ifmo.app.lib.commands.AddCommand;
import ru.ifmo.app.lib.commands.AddIfMaxCommand;
import ru.ifmo.app.lib.commands.ClearCommand;
import ru.ifmo.app.lib.commands.CountGreaterThanFuelTypeCommand;
import ru.ifmo.app.lib.commands.ExecuteScriptCommand;
import ru.ifmo.app.lib.commands.ExitCommand;
import ru.ifmo.app.lib.commands.FilterGreaterThanFuelTypeCommand;
import ru.ifmo.app.lib.commands.GroupCountingByIdCommand;
import ru.ifmo.app.lib.commands.HeadCommand;
import ru.ifmo.app.lib.commands.HelpCommand;
import ru.ifmo.app.lib.commands.InfoCommand;
import ru.ifmo.app.lib.commands.RemoveByIdCommand;
import ru.ifmo.app.lib.commands.RemoveLowerCommand;
import ru.ifmo.app.lib.commands.SaveCommand;
import ru.ifmo.app.lib.commands.ShowCommand;
import ru.ifmo.app.lib.commands.UpdateCommand;
import ru.ifmo.app.lib.exceptions.CommandParseException;
import ru.ifmo.app.lib.exceptions.ExitProgramException;
import ru.ifmo.app.lib.exceptions.InvalidArgumentException;
import ru.ifmo.app.lib.exceptions.InvalidNumberOfArgumentsException;
import ru.ifmo.app.lib.exceptions.MaximumScriptExecutionDepthException;
import ru.ifmo.app.lib.utils.DeprecatedCommandRegistery;
import ru.ifmo.app.lib.utils.Levenshtein;
import ru.ifmo.app.lib.utils.Messages;

/**
 * A class that manages the execution of the commands, given the Xml file containing collection
 * elements, the {@link Vehicles} object and a scanner to handle user input. Contains a
 * {@link DeprecatedCommandRegistery} which is added internelly during construction.
 */
public class CommandExecutor {
  private Scanner scanner;
  private Vehicles vehicles;
  private File vehiclesFile;
  private int scriptExecutionDepth;

  /**
   * A registery which associates certain command strings (e.g. "help", "h") with {@link DeprecatedCommand
   * Commands}. Created during CommandExecutor construction.
   */
  private DeprecatedCommandRegistery commandRegistery;

  /**
   * Class constructor
   * 
   * @param scanner Scanner which will be used to get the user input
   * @param vehicles Initial {@link Vehicles} which will be used
   * @param vehiclesFile A path to the file which was selected by the user
   */
  public CommandExecutor(Scanner scanner, Vehicles vehicles, File vehiclesFile,
      int scriptExecutionDepth) {
    this.scanner = scanner;
    this.vehicles = vehicles;
    this.vehiclesFile = vehiclesFile;
    this.scriptExecutionDepth = scriptExecutionDepth;

    this.commandRegistery = new DeprecatedCommandRegistery().put(new HelpCommand(), "help", "h")
        .put(new InfoCommand(), "info", "i").put(new AddCommand(), "add", "a")
        .put(new ShowCommand(), "show", "s").put(new UpdateCommand(), "update", "u")
        .put(new RemoveByIdCommand(), "remove_by_id", "r").put(new ClearCommand(), "clear")
        .put(new ExitCommand(), "exit", "e", "q").put(new HeadCommand(), "head")
        .put(new AddIfMaxCommand(), "add_if_min").put(new RemoveLowerCommand(), "remove_lower")
        .put(new ExecuteScriptCommand(), "execute_script")
        .put(new CountGreaterThanFuelTypeCommand(), "count_greater_than_fuel_type")
        .put(new FilterGreaterThanFuelTypeCommand(), "filter_greater_than_fuel_type")
        .put(new GroupCountingByIdCommand(), "group_counting_by_id").put(new SaveCommand(), "save");
  }

  /**
     * Parses a String and returns the command with it's arguments.
     * 
     * @param commandString A string, given to the program as command (e.g. "update 123")
     * @return An entry, where the {@link SimpleEntry#getKey key} is the command name and the {@link SimpleEntry#getValue() value} is an array of arguments (represented as strings)
     * @throws CommandParseException Thrown if the command is empty
     */
    private static SimpleEntry<String, String[]> parseCommandString(String commandString) throws CommandParseException {
        String[] splitted = commandString.trim().split("\s+");
        if (splitted.length == 0 || splitted[0].length() == 0) {
            throw new CommandParseException(commandString);
        }
        
        var command = splitted[0];
        var arguments = Arrays.copyOfRange(splitted, 1, splitted.length);
        
        return new SimpleEntry<>(command, arguments);
    }

  private List<String> getPossiblyMeantCommands(String inputtedCommandname) {
    return this.commandRegistery.getAllCommands().stream()
        .filter(possibleCommand -> possibleCommand.getKey().size() > 0)
        .map(possibleCommand -> possibleCommand.getKey().iterator().next())
        .filter(possibleCommandName -> {
          return Levenshtein.distance(inputtedCommandname, possibleCommandName) < 3;
        }).toList();
  }

  /**
   * Given a command string this method parses a command with
   * {@link CommandExecutor#parseCommandString(String)} and then executes the command if it was
   * found in the {@link DeprecatedCommandRegistery} via the {@link DeprecatedCommand#execute(DeprecatedCommandContext)} method
   * 
   * @param commandString Inputted command string (e.g. "update 123")
   * @throws ExitProgramException Thrown if the user inputted a command like "exit"
   * @throws MaximumScriptExecutionDepthException Thrown if the execute_script command depth exceeds
   *         maximum
   */
  public void executeCommandString(String commandString)
      throws ExitProgramException, MaximumScriptExecutionDepthException {
    if (commandString.trim().length() == 0)
      return;
    try {
      var pair = CommandExecutor.parseCommandString(commandString);
      var commandname = pair.getKey();
      var arguments = pair.getValue();

      DeprecatedCommand command = this.commandRegistery.get(commandname);
      if (command == null) {
        App.logger.warn(Messages.get("Error.CommandNotFound", commandname));

        var possibleCommands = getPossiblyMeantCommands(commandname);

        if (!possibleCommands.isEmpty()) {
          App.logger.warn(Messages.get("Error.MaybeYouMeant", ""));

          possibleCommands.forEach(possibleCommand -> {
            App.logger.info("  " + possibleCommand);
          });
        }

        return;
      }

      try {
        command.execute(new DeprecatedCommandContext(arguments, this.vehicles, this.vehiclesFile,
            this.scanner, this.commandRegistery, this.scriptExecutionDepth));
      } catch (InvalidNumberOfArgumentsException | InvalidArgumentException err) {
        App.logger.error(err.getMessage());
      }
    } catch (CommandParseException err) {
      App.logger.error(err.getMessage());
    }
  }
}
