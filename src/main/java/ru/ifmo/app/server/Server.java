package ru.ifmo.app.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.ClientRequest;
import ru.ifmo.app.shared.ServerResponse;
import ru.ifmo.app.shared.Utils;
import ru.ifmo.app.shared.Vehicles;
import ru.ifmo.app.shared.Vehicles.VehicleCreationSchema;
import ru.ifmo.app.shared.commands.CommandParameters;
import ru.ifmo.app.shared.entities.Coordinates;
import ru.ifmo.app.shared.entities.FuelType;
import ru.ifmo.app.shared.entities.VehicleType;

class NoClientRequestException extends Exception {}

public class Server {

    public static final Logger logger = LoggerFactory.getLogger(
        "ru.ifmo.app.server.logger"
    );

    private static ClientRequest<CommandParameters> getClientRequestFromStream(
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
        try (
            var byteOutput = new ByteArrayOutputStream();
            var writer = new PrintWriter(byteOutput)
        ) {
            ClientRequest<CommandParameters> request = null;

            try {
                request = Server.getClientRequestFromStream(in);
                executor.execute(request, writer);
            } catch (InvalidCommandParametersException err) {
                writer.println(
                    "Invalid parameter object was passed to the command: " + err
                );
            } catch (ExitProgramException err) {
                writer.println("Exiting");
                return true;
            } catch (NoClientRequestException err) {
                writer.println("No message from client, disconnecting...");
                return true;
            }

            var error = writer.checkError();
            if (error) {
                Server.logger.error("Error in print writer occured");
            }

            writer.flush();
            var response = new ServerResponse(
                new String(byteOutput.toByteArray(), StandardCharsets.UTF_8)
            );
            var responseBuffer = Utils.objectToBuffer(response);
            out.write(responseBuffer.array());
        }

        return false;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
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
                    vehicles.add(
                        new VehicleCreationSchema(
                            "name1",
                            new Coordinates(1l, 2),
                            1.1f,
                            VehicleType.BICYCLE,
                            FuelType.ALCOHOL
                        )
                    );
                    vehicles.add(
                        new VehicleCreationSchema(
                            "NAME2",
                            new Coordinates(2l, 3),
                            2.2f,
                            VehicleType.BOAT,
                            FuelType.GASOLINE
                        )
                    );
                    vehicles.add(
                        new VehicleCreationSchema(
                            "n3m3",
                            new Coordinates(3l, 4),
                            3.3f,
                            VehicleType.DRONE,
                            FuelType.KEROSENE
                        )
                    );
                    var executor = new CommandExecutor(vehicles);

                    while (true) {
                        boolean clientQuit = Server.handleClient(in, out, executor);
                        if (clientQuit) break;
                    }
                }
            }
        }
    }
}
