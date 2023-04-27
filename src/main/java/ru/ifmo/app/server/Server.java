package ru.ifmo.app.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

class NoClientRequestException extends Exception {}

class ServerRunnable implements Runnable {

    private List<Vehicles> activeCollections;

    public ServerRunnable(List<Vehicles> collections) {
        this.activeCollections = collections;
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
        } catch (BufferUnderflowException err) {
            throw new NoClientRequestException();
        }
    }

    private static boolean handleClient(
        InputStream in,
        OutputStream out,
        CommandExecutor executor
    ) throws IOException, ClassNotFoundException {
        boolean clientDisconnected = false;
        try (
            var byteOutput = new ByteArrayOutputStream();
            var writer = new PrintWriter(byteOutput)
        ) {
            ClientRequest<CommandParameters, Serializable> request = null;
            try {
                request = ServerRunnable.getClientRequestFromStream(in);
                if (executor.commandsExecuted == 0) {
                    String path = request.pathToSavefile();
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
                Server.saveCollection(
                    executor.vehicles,
                    request.pathToSavefile(),
                    writer
                );
                clientDisconnected = true;
            } catch (NoClientRequestException err) {
                writer.println("No message from client, disconnecting...");
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
            out.write(responseBuffer.array());
        }
        return clientDisconnected;
    }

    @Override
    public void run() {
        int port = 1111;
        try (var server = new ServerSocket(port);) {
            Server.logger.info("Server started at port: " + port);
            while (true) {
                try (
                    var client = server.accept();
                    var out = client.getOutputStream();
                    var in = client.getInputStream();
                ) {
                    Server.logger.info("Client connected: " + client.getInetAddress());

                    Vehicles vehicles = new Vehicles();
                    activeCollections.add(vehicles);
                    var executor = new CommandExecutor(vehicles);

                    while (true) {
                        try {
                            boolean clientQuit = ServerRunnable.handleClient(
                                in,
                                out,
                                executor
                            );
                            if (clientQuit) {
                                activeCollections.remove(vehicles);
                                break;
                            }
                        } catch (ClassNotFoundException err) {
                            Server.logger.error(
                                "Unknown class in client request, skipping..."
                            );
                        }
                    }
                }
            }
        } catch (IOException err) {
            Server.logger.error("IO Exception occured: " + err.getMessage());
        }
    }
}

public class Server {

    public static final Logger logger = LoggerFactory.getLogger(
        "ru.ifmo.app.server.logger"
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

    public static void main(String[] __) throws IOException, ClassNotFoundException {
        List<Vehicles> collections = new ArrayList<>();
        var serverThread = new Thread(new ServerRunnable(collections));
        serverThread.start();

        try (var scanner = new Scanner(System.in)) {
            while (true) {
                try {
                    String commandName = scanner.nextLine();
                    var parsed = Server.parseCommand(commandName);
                    String command = parsed.getKey();
                    List<String> args = parsed.getValue();
                    if (command.equals("save")) {
                        if (collections.size() == 0) {
                            Server.logger.info("No active collections");
                            continue;
                        }
                        if (args.size() == 0) {
                            Server.logger.info("Expected a path in arguments");
                            continue;
                        }
                        Server.saveCollection(collections.get(0), args.get(0), null);
                    }
                } catch (CommandParseException err) {
                    Server.logger.error("Couldn't parse command: " + err.getMessage());
                }
            }
        }
    }
}
