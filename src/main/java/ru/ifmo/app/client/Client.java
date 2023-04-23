package ru.ifmo.app.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
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

// TODO: Organise code in 'server', 'client' and 'shared' packages properly, so it would be possible
// to compile them seperately from each other (e.g only 'server' & 'shared' or only 'client' & 'shared')

public class Client {

    public static final Logger logger = LoggerFactory.getLogger(
        "ru.ifmo.app.client.logger"
    );

    private static SimpleEntry<String, List<String>> parseCommand(String commandString)
        throws CommandParseException {
        var splitted = Arrays.asList(commandString.trim().split("\s+"));
        if (splitted.size() == 0) {
            throw new CommandParseException("Expected a command, but got nothing");
        }
        String commandName = splitted.get(0);
        List<String> commandArguments = splitted.subList(1, splitted.size());
        return new SimpleEntry<String, List<String>>(commandName, commandArguments);
    }

    private static ClientRequest<CommandParameters, Serializable> constructRequest(
        String commandString,
        Scanner scanner,
        boolean logScanned
    ) throws CommandParseException, InvalidCommandParametersException {
        SimpleEntry<String, List<String>> parsed = Client.parseCommand(commandString);
        var commandName = parsed.getKey();
        var commandArguments = parsed.getValue();
        Command command = CommandRegistery.global.get(commandName);
        if (command == null) {
            throw new CommandParseException("Command '" + commandName + "' not found");
        }
        CommandParameters params = command.parametersObjectFromStrings(
            commandArguments.toArray(new String[commandArguments.size()])
        );
        Serializable additionalObject = command.additionalObjectFromScanner(
            scanner,
            logScanned
        );
        return new ClientRequest<>(commandName, params, additionalObject);
    }

    private static boolean isExecuteScriptCommand(String commandString)
        throws CommandParseException {
        var commandName = Client.parseCommand(commandString).getKey();
        return commandName.equals("execute_script");
    }

    private static Scanner scannerFromExecuteScriptCommand(String commandString)
        throws CommandParseException {
        var arguments = Client.parseCommand(commandString).getValue();
        if (arguments.size() == 0) throw new CommandParseException(
            "Filepath to a script expected, got nothing"
        );
        var file = new File(arguments.get(0));
        try {
            return new Scanner(new FileInputStream(file.getAbsolutePath()));
        } catch (FileNotFoundException err) {
            throw new CommandParseException(
                "File '" + file.toPath() + "' is not readable: " + err.getMessage()
            );
        }
    }

    public static void main(String[] args)
        throws IOException, ClassNotFoundException, InterruptedException {
        int port = 1111;
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", port);

        try (
            SocketChannel client = SocketChannel.open(addr);
            Selector selector = Selector.open();
            Scanner inputScanner = new Scanner(System.in);
        ) {
            Scanner scriptScanner = null;
            client.configureBlocking(false);
            int ops = client.validOps();
            client.register(selector, ops);
            Client.logger.info("Connected to " + addr + "...");

            clientLoop:while (true) {
                Scanner currentScanner = scriptScanner == null
                    ? inputScanner
                    : scriptScanner;
                Client.logger.info("> ");
                String commandString = null;
                try {
                    commandString = currentScanner.nextLine();
                } catch (NoSuchElementException err) {
                    Client.logger.info("End of file, execution ended...");
                    scriptScanner.close();
                    scriptScanner = null;
                    continue;
                }

                ClientRequest<CommandParameters, Serializable> requestObject = null;
                try {
                    if (Client.isExecuteScriptCommand(commandString)) {
                        scriptScanner =
                            Client.scannerFromExecuteScriptCommand(commandString);
                        continue;
                    }

                    requestObject =
                        Client.constructRequest(
                            commandString,
                            currentScanner,
                            currentScanner == scriptScanner
                        );
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
                                if (response.clientDisconnected()) {
                                    Client.logger.info("Exiting client");
                                    break clientLoop;
                                }
                                Client.logger.info(response.output());
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
