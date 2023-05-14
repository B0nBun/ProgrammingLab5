package ru.ifmo.app.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.app.client.exceptions.CommandParseException;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.ClientRequest;
import ru.ifmo.app.shared.ServerResponse;
import ru.ifmo.app.shared.Utils;
import ru.ifmo.app.shared.Vehicles;
import ru.ifmo.app.shared.commands.CommandParameters;

class NoClientRequestException extends Exception {

    public NoClientRequestException(String message) {
        super(message);
    }
}

public class Server {

    public static final Logger logger = LoggerFactory.getLogger(
        "ru.ifmo.app.server.logger"
    );

    public static void main(String[] __) throws IOException, ClassNotFoundException {
        var socketServerRunnable = new SocketServerRunnable();
        var serverThread = new Thread(socketServerRunnable);
        serverThread.start();

        try (var scanner = new Scanner(System.in)) {
            while (true) {
                try {
                    String commandName = scanner.nextLine();
                    var parsed = Server.parseCommand(commandName);
                    String command = parsed.getKey();
                    if (command.equals("save")) {
                        if (socketServerRunnable.serverContext.activeCollection == null) {
                            Server.logger.info("No active collections");
                            continue;
                        }
                        if (
                            socketServerRunnable.serverContext.pathsToSavefiles.size() ==
                            0
                            // socketServerRunnable.serverContext.pathToSavefile == null
                        ) {
                            Server.logger.info(
                                "No clients provided their savefiles yet (you can provide one in arguments to this command)"
                            );
                            continue;
                        }
                        for (var addrPath : socketServerRunnable.serverContext.pathsToSavefiles.entrySet()) {
                            Server.saveCollection(
                                socketServerRunnable.serverContext.activeCollection.get(
                                    addrPath.getKey()
                                ),
                                addrPath.getValue(),
                                null
                            );
                        }
                    }
                } catch (CommandParseException err) {
                    Server.logger.error("Couldn't parse command: " + err.getMessage());
                } catch (NoSuchElementException err) {
                    Server.logger.error("Exiting...");
                    System.exit(0);
                }
            }
        }
    }

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

    public static Vehicles loadCollection(String filepath, PrintWriter outputWriter)
        throws IOException {
        if (filepath == null || filepath.trim().length() == 0) {
            Server.logger.error("Expected a filepath, but got nothing");
            return new Vehicles();
        }

        var file = new File(filepath);
        try (var inputStream = new FileInputStream(file)) {
            Vehicles loaded = Vehicles.loadFromXml(inputStream);
            return loaded;
        } catch (JDOMException err) {
            Server.logger.error("Xml file parsing error: " + err.getMessage());
            outputWriter.println("Xml file parsing error: " + err.getMessage());
            return new Vehicles();
        } catch (FileNotFoundException err) {
            Server.logger.error("File not found exception: " + err.getMessage());
            outputWriter.println("File not found exception: " + err.getMessage());
            return new Vehicles();
        }
    }

    public static void saveCollection(
        Vehicles collection,
        String filepath,
        PrintWriter outputWriter
    ) {
        if (filepath == null || filepath.trim().length() == 0) {
            Server.logger.error("Expected a filepath in arguments");
        }

        var xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        Element vehiclesRootElement = collection.toXmlElement();
        String vehiclesSerialized = xmlOutputter.outputString(vehiclesRootElement);

        var file = new File(Utils.expandPath(filepath));
        try (var printWriter = new PrintWriter(file)) {
            printWriter.write(vehiclesSerialized);
            Server.logger.info(
                "Collection was saved to '" + file.getAbsolutePath() + "'"
            );
            if (outputWriter != null) outputWriter.write(
                "Collection was save to '" + file.getAbsolutePath() + "'"
            );
        } catch (FileNotFoundException err) {
            Server.logger.info("Couldn't write to the file: " + err.getMessage());
            if (outputWriter != null) outputWriter.write(
                "Couldn't write to the file: " + err.getMessage()
            );
        }
    }
}

class ServerContext {

    public Map<SocketAddress, Vehicles> activeCollection = new HashMap<>();
    public Map<SocketAddress, String> pathsToSavefiles = new HashMap<>();
}

class SocketServerRunnable implements Runnable {

    public ServerContext serverContext;

    public SocketServerRunnable() {
        this.serverContext = new ServerContext();
    }

    @Override
    public void run() {
        int port = 1111;
        try (var server = new ServerSocket(port);) {
            Server.logger.info("Server started at port: " + port);
            while (true) {
                Socket client = server.accept();
                Server.logger.info("Client connected: " + client.getInetAddress());
                var collection = new Vehicles();
                serverContext.activeCollection.put(
                    client.getRemoteSocketAddress(),
                    collection
                );
                var handler = new ClientHandler(client, serverContext, collection);

                new Thread(handler).start();
            }
        } catch (IOException err) {
            Server.logger.error("IO Exception occured: " + err.getMessage());
        }
    }
}

class ClientHandler implements Runnable {

    private Socket client;
    private ServerContext serverContext;
    private Vehicles collection;

    public ClientHandler(Socket socket, ServerContext context, Vehicles collection) {
        this.client = socket;
        this.serverContext = context;
        this.collection = collection;
    }

    @Override
    public void run() {
        Server.logger.info("Client thread started: " + client.getInetAddress());

        var executor = new CommandExecutor(this.collection, null);

        while (true) {
            try {
                boolean clientQuit = ClientHandler.handleClient(
                    this.client,
                    executor,
                    this.serverContext
                );
                if (clientQuit) {
                    this.serverContext.pathsToSavefiles.remove(
                            this.client.getRemoteSocketAddress()
                        );
                    this.serverContext.activeCollection.remove(
                            this.client.getRemoteSocketAddress()
                        );
                    break;
                }
            } catch (ClassNotFoundException err) {
                Server.logger.error("Unknown class in client request, skipping...");
            } catch (IOException err) {
                Server.logger.error("IOException in client handler: " + err.getMessage());
            }
        }
    }

    private static boolean handleClient(
        Socket client,
        CommandExecutor executor,
        ServerContext serverContext
    ) throws IOException, ClassNotFoundException {
        boolean clientDisconnected = false;
        try (
            var byteOutput = new ByteArrayOutputStream();
            var writer = new PrintWriter(byteOutput)
        ) {
            ClientRequest<CommandParameters, Serializable> request = null;
            try {
                request =
                    ClientHandler.getClientRequestFromStream(client.getInputStream());
                if (executor.commandsExecuted == 0) {
                    String path = request.pathToSavefile();
                    serverContext.pathsToSavefiles.put(
                        client.getRemoteSocketAddress(),
                        path
                    );
                    // serverContext.pathsToSavefiles.add(path);
                    executor.vehicles.replaceCollectionWith(
                        Server.loadCollection(path, writer)
                    );
                }

                executor.execute(request, writer);
            } catch (InvalidCommandParametersException err) {
                writer.println(
                    "Invalid parameter object was passed to the command: " + err
                );
            } catch (ExitProgramException err) {
                if (request != null) {
                    serverContext.pathsToSavefiles.remove(
                        client.getRemoteSocketAddress()
                    );
                    Server.saveCollection(
                        executor.vehicles,
                        request.pathToSavefile(),
                        writer
                    );
                }
                clientDisconnected = true;
            } catch (NoClientRequestException err) {
                writer.println(
                    "No message from client (" + err.getMessage() + "), disconnecting..."
                );
                clientDisconnected = true;
            }

            var error = writer.checkError();
            if (error) {
                Server.logger.error("Error in print writer occured");
            }

            writer.flush();
            var response = new ServerResponse(
                new String(byteOutput.toByteArray(), StandardCharsets.UTF_8),
                clientDisconnected
            );
            var responseBuffer = Utils.objectToBuffer(response);
            client.getOutputStream().write(responseBuffer.array());
        }
        return clientDisconnected;
    }

    private static ClientRequest<CommandParameters, Serializable> getClientRequestFromStream(
        InputStream in
    ) throws IOException, ClassNotFoundException, NoClientRequestException {
        try {
            byte[] sizeBytes = in.readNBytes(Integer.BYTES);
            int objectSize = ByteBuffer.wrap(sizeBytes).getInt();
            byte[] objectBytes = in.readNBytes(objectSize);
            var bytesInput = new ByteArrayInputStream(objectBytes);
            var objectInput = new ObjectInputStream(bytesInput);
            var message = ClientRequest.uncheckedCast(objectInput.readObject());
            bytesInput.close();
            objectInput.close();
            return message;
        } catch (
            BufferUnderflowException | ClassNotFoundException | InvalidClassException err
        ) {
            throw new NoClientRequestException(err.getMessage());
        }
    }
}
