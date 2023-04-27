package ru.ifmo.app.server;

import java.io.PrintWriter;
import java.io.Serializable;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.ClientRequest;
import ru.ifmo.app.shared.CommandRegistery;
import ru.ifmo.app.shared.Vehicles;
import ru.ifmo.app.shared.commands.CommandParameters;

public class CommandExecutor {

    private CommandRegistery commandRegistery = CommandRegistery.global;
    public int commandsExecuted = 0;
    public Vehicles vehicles;
    public String savefilePath;

    public CommandExecutor(Vehicles vehicles, String savefilePath) {
        this.vehicles = vehicles;
        this.savefilePath = savefilePath;
    }

    public void execute(
        ClientRequest<CommandParameters, Serializable> clientRequest,
        PrintWriter outputWriter
    ) throws InvalidCommandParametersException, ExitProgramException {
        String commandName = clientRequest.commandName();
        var command = this.commandRegistery.get(commandName);
        if (command == null) {
            outputWriter.println(
                "Couldn't find the command " + clientRequest.commandName()
            );
        }
        command.execute(
            new CommandContext(this.commandRegistery, outputWriter, this.vehicles),
            clientRequest.commandParameters(),
            clientRequest.additionalObject()
        );
        this.commandsExecuted++;
    }
}
