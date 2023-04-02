package ru.ifmo.app.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import ru.ifmo.app.lib.ClientMessage;
import ru.ifmo.app.lib.ServerResponse;
import ru.ifmo.app.lib.Utils;

// TODO: Ctrl+C on client causes BufferUnderflowException
public class Server {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    int port = 1111;
    try (var server = new ServerSocket(port);) {
      while (true) {
        try (var client = server.accept();
            var out = client.getOutputStream();
            var in = client.getInputStream();) {
          System.out.println("Client connected: " + client.getInetAddress());

          boolean clientQuit = false;
          while (!clientQuit) {
            byte[] sizeBytes = in.readNBytes(Integer.BYTES);
            int objectSize = ByteBuffer.wrap(sizeBytes).getInt();
            byte[] objectBytes = in.readNBytes(objectSize);
            var bytesInput = new ByteArrayInputStream(objectBytes);
            var objectInput = new ObjectInputStream(bytesInput);
            var message = (ClientMessage) objectInput.readObject();
            bytesInput.close();
            objectInput.close();

            System.out.println("Server got message: " + message);
            var response = ServerResponse.success("Server got the number: " + message.value());
            var responseBuffer = Utils.objectToBuffer(response);
            out.write(responseBuffer.array());

            if (message.message().equals("quit")) {
              System.out.println("Client quit");
              clientQuit = true;
            }
          }
        }
      }
    }
  }
}
