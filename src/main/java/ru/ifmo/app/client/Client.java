package ru.ifmo.app.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.app.client.exceptions.CommandParseException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.ClientRequest;
import ru.ifmo.app.shared.CommandRegistery;
import ru.ifmo.app.shared.ServerResponse;
import ru.ifmo.app.shared.Utils;
import ru.ifmo.app.shared.commands.Command;
import ru.ifmo.app.shared.commands.CommandParameters;

/**
 * Commands TODO: add; add if max; execute script; filetr greater than fuel type; group counting by
 * id; head; info; remove; lower; save; show; update
 */

public class Client {

    public static final Logger logger = LoggerFactory.getLogger(
        "ru.ifmo.app.client.logger"
    );

    private static ClientRequest<CommandParameters> requestFromCommandString(
        String commandString
    ) throws CommandParseException, InvalidCommandParametersException {
        var splitted = Arrays.asList(commandString.trim().split("\s+"));
        if (splitted.size() == 0) {
            throw new CommandParseException("Expected a command, but got nothing");
        }
        String commandName = splitted.get(0);
        List<String> commandArguments = splitted.subList(1, splitted.size());
        Command command = CommandRegistery.global.get(commandName);
        if (command == null) {
            throw new CommandParseException("Command not found");
        }
        CommandParameters params = command.parametersObjectFromStrings(
            commandArguments.toArray(new String[commandArguments.size()])
        );
        return new ClientRequest<>(commandName, params);
    }

    public static void main(String[] args)
        throws IOException, ClassNotFoundException, InterruptedException {
        int port = 1111;
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", port);

        try (
            SocketChannel client = SocketChannel.open(addr);
            Selector selector = Selector.open();
            Scanner scanner = new Scanner(System.in);
        ) {
            client.configureBlocking(false);
            int ops = client.validOps();
            client.register(selector, ops);
            Client.logger.info("Connected to " + addr + "...");

            while (true) {
                Client.logger.info("Input the message for the server: ");
                var commandString = scanner.nextLine();
                ClientRequest<CommandParameters> requestObject = null;
                try {
                    requestObject = Client.requestFromCommandString(commandString);
                } catch (InvalidCommandParametersException err) {
                    Client.logger.error(
                        "Invalid command parameters: " + err.getMessage()
                    );
                    continue;
                } catch (CommandParseException err) {
                    Client.logger.error(
                        "Couldn't parse the command: " + err.getMessage()
                    );
                    continue;
                }

                // TODO: Add timeout to avoid infinite loop
                keysHandling:while (true) {
                    selector.select();
                    var keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        var key = keys.next();
                        keys.remove();

                        if (key.isWritable()) {
                            ByteBuffer buffer = Utils.objectToBuffer(requestObject);
                            buffer.flip();
                            client.write(buffer);
                            key.interestOps(SelectionKey.OP_READ);
                        } else if (key.isReadable()) {
                            try {
                                ServerResponse response = Utils.objectFromChannel(
                                    client,
                                    ServerResponse.class::cast
                                );
                                Client.logger.info("Response: \n" + response.output());
                                key.interestOps(SelectionKey.OP_WRITE);
                            } catch (BufferUnderflowException err) {
                                Client.logger.warn("Couldn't connect to the server...");
                            }
                            break keysHandling;
                        }
                    }
                }
            }
        }
    }
}
