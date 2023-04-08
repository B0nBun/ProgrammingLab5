package ru.ifmo.app.server;

import java.io.PrintWriter;
import java.io.Serializable;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.shared.ClientRequest;
import ru.ifmo.app.shared.CommandRegistery;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;

public class CommandExecutor {
  private CommandRegistery commandRegistery = CommandRegistery.global;
  private Vehicles vehicles;

  public CommandExecutor(Vehicles vehicles) {
    this.vehicles = vehicles;
  }

  public void execute(ClientRequest<Serializable> clientMessage, PrintWriter outputWriter)
      throws InvalidCommandParametersException, ExitProgramException {
    String commandName = clientMessage.commandName();
    var command = this.commandRegistery.get(commandName);
    if (command == null) {
      outputWriter.println("Couldn't find the command " + clientMessage.commandName());
    }
    command.execute(new CommandContext(this.commandRegistery, outputWriter, this.vehicles),
        clientMessage.commandParameters());
  }
}
