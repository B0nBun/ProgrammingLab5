package ru.ifmo.app.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
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
import java.util.Stack;
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
        String pathToSavefile,
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
        try {
            Serializable additionalObject = command.additionalObjectFromScanner(
                scanner,
                logScanned
            );
            return new ClientRequest<>(
                commandName,
                pathToSavefile,
                params,
                additionalObject
            );
        } catch (NoSuchElementException err) {
            throw new InvalidCommandParametersException("Premature end of file");
        }
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
        try {
            return new Scanner(new File(arguments.get(0)));
        } catch (FileNotFoundException err) {
            throw new CommandParseException("File not found: " + err.getMessage());
        }
    }

    private static record Connection(
        SocketChannel client,
        Integer ops,
        Selector selector
    ) {
        public static Connection fromChannel(SocketChannel channel) throws IOException {
            Selector selector = Selector.open();
            channel.configureBlocking(false);
            int ops = channel.validOps();
            channel.register(selector, ops);
            return new Connection(channel, ops, selector);
        }

        public void close() throws IOException {
            this.selector.close();
            this.client.close();
        }
    }

    public static void main(String[] args)
        throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length == 0) {
            Client.logger.error("Expected a path to xml collection in command arguments");
            return;
        }
        String pathToSavefile = args[0];

        int port = 1111;
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", port);

        try (
            SocketChannel _client = SocketChannel.open(addr);
            Scanner inputScanner = new Scanner(System.in);
        ) {
            // Scanner active during script execution
            var scriptPaths = new Stack<Scanner>();
            // Connection object, through which socket operations are made
            Connection connection = Connection.fromChannel(_client);
            Client.logger.info("Connected to " + addr + "...");

            clientLoop:while (true) {
                Scanner currentScanner = scriptPaths.empty()
                    ? inputScanner
                    : scriptPaths.peek();

                // Scanning the command string
                Client.logger.info("> ");
                String commandString = null;
                try {
                    commandString = currentScanner.nextLine();
                    if (currentScanner != inputScanner) {
                        Client.logger.info(commandString);
                    }
                } catch (NoSuchElementException err) {
                    if (currentScanner != inputScanner) {
                        Client.logger.info("End of file, execution ended...");
                        currentScanner.close();
                        scriptPaths.pop();
                        continue;
                    } else {
                        break;
                    }
                }

                // Constructing the client request
                ClientRequest<CommandParameters, Serializable> requestObject = null;
                try {
                    if (Client.isExecuteScriptCommand(commandString)) {
                        scriptPaths.push(
                            Client.scannerFromExecuteScriptCommand(commandString)
                        );
                        continue;
                    }

                    requestObject =
                        Client.constructRequest(
                            commandString,
                            pathToSavefile,
                            currentScanner,
                            currentScanner != inputScanner
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

                keysHandling:while (true) {
                    connection.selector().select();
                    var keys = connection.selector().selectedKeys().iterator();

                    // Handling the connection (reading/writing)
                    while (keys.hasNext()) {
                        var key = keys.next();
                        keys.remove();

                        if (key.isWritable()) {
                            ByteBuffer buffer = Utils.objectToBuffer(requestObject);
                            buffer.flip();
                            connection.client().write(buffer);
                            key.interestOps(SelectionKey.OP_READ);
                        } else if (key.isReadable()) {
                            try {
                                ServerResponse response = Utils.objectFromChannel(
                                    connection.client(),
                                    ServerResponse.class::cast
                                );
                                if (response.clientDisconnected()) {
                                    Client.logger.info(response.output());
                                    Client.logger.info("Exiting client");
                                    break clientLoop;
                                }
                                Client.logger.info(response.output());
                                key.interestOps(SelectionKey.OP_WRITE);
                            } catch (BufferUnderflowException err) {
                                Client.logger.warn("Couldn't connect to the server...");
                                Client.logger.info("Trying to reconnect");
                                var previous = connection;
                                try {
                                    Connection newConnection = Connection.fromChannel(
                                        SocketChannel.open(addr)
                                    );
                                    connection = newConnection;
                                    previous.close();
                                    Client.logger.info("Connected! Input your command: ");
                                } catch (ConnectException cerr) {
                                    Client.logger.error(
                                        "Connection refused: " + cerr.getMessage()
                                    );
                                }
                            }
                            break keysHandling;
                        }
                    }
                }
            }
        } catch (ConnectException err) {
            Client.logger.error("Connection refused: " + err.getMessage());
        }
    }
}
