package ru.ifmo.app.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.server.exceptions.ExitProgramException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.ClientMessage;
import ru.ifmo.app.shared.ServerResponse;

// TODO: Ctrl+C on client causes BufferUnderflowException
public class Server {
  private static ClientMessage<Object> getClientMessageFromStream(InputStream in)
      throws IOException, ClassNotFoundException {
    byte[] sizeBytes = in.readNBytes(Integer.BYTES);
    int objectSize = ByteBuffer.wrap(sizeBytes).getInt();
    byte[] objectBytes = in.readNBytes(objectSize);
    var bytesInput = new ByteArrayInputStream(objectBytes);
    var objectInput = new ObjectInputStream(bytesInput);
    var message = ClientMessage.uncheckedCast(objectInput.readObject());
    bytesInput.close();
    objectInput.close();
    return message;
  }

  private static boolean handleClient(InputStream in, OutputStream out, CommandExecutor executor)
      throws IOException, ClassNotFoundException {

    var message = Server.getClientMessageFromStream(in);

    try (var byteOutput = new ByteArrayOutputStream(); var writer = new PrintWriter(byteOutput)) {
      try {
        executor.execute(message, writer);
      } catch (InvalidCommandParametersException err) {
        writer.println("Invalid parameter object was passed to the command: " + err);
      } catch (ExitProgramException err) {
        writer.println("Exiting");
        return true;
      }

      var error = writer.checkError();
      if (error) {
        System.out.println("Error in print writer occured");
      }

      writer.flush();
      var response =
          new ServerResponse(new String(byteOutput.toByteArray(), StandardCharsets.UTF_8));
      var responseBuffer = Utils.objectToBuffer(response);
      out.write(responseBuffer.array());
    }

    return false;
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    int port = 1111;
    try (var server = new ServerSocket(port);) {
      System.out.println("Server started at port: " + port);
      while (true) {
        try (var client = server.accept();
            var out = client.getOutputStream();
            var in = client.getInputStream();) {
          System.out.println("Client connected: " + client.getInetAddress());

          Vehicles vehicles = new Vehicles();
          var executor = new CommandExecutor(vehicles);

          while (true) {
            boolean clientQuit = Server.handleClient(in, out, executor);
            if (clientQuit)
              break;
          }
        }
      }
    }
  }
}
