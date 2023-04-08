package ru.ifmo.app.shared.commands;

import java.io.Serializable;
import ru.ifmo.app.lib.utils.Messages;
import ru.ifmo.app.server.CommandContext;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.Command;

public class RemoveByIdCommand implements Command {
  public static class Parameters implements Serializable {
    String idString;

    public Parameters(String idString) {
      this.idString = idString;
    }
  }

  @Override
  public void execute(CommandContext context, Object commandParameters)
      throws InvalidCommandParametersException, ExitProgramException {
    try {
      var parameters = (Parameters) commandParameters;
      var idString = parameters.idString;

      var found = context.vehicles().removeIf(v -> {
        if (v.id().toString().startsWith(idString)) {
          context.outputWriter().println(Messages.get("RemovingVehicleWithId", v.id()));
          return true;
        }
        return false;
      });

      if (!found) {
        context.outputWriter()
            .println(Messages.get("Warn.VehicleStartingWithIdNotFound", idString));
      }
    } catch (ClassCastException err) {
      throw new InvalidCommandParametersException();
    }
  }

  @Override
  public String[] helpArguments() {
    return new String[] {Messages.get("Help.Command.Arg.Id")};
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.RemoveById");
  }
}
