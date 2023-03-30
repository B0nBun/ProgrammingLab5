package ru.ifmo.app.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import ru.ifmo.app.lib.ClientMessage;
import ru.ifmo.app.lib.ServerResponse;
import ru.ifmo.app.lib.Utils;

public class Client {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    int port = 1111;
    InetSocketAddress addr = new InetSocketAddress("127.0.0.1", port);

    try (SocketChannel client = SocketChannel.open(addr);
        Scanner scanner = new Scanner(System.in);) {
      System.out.println("Connecting to " + addr + "...");
      while (true) {
        var input = scanner.nextLine();
        var clientMessage = new ClientMessage(input, input.length());
        ByteBuffer buffer = Utils.objectToBuffer(clientMessage);
        client.write(buffer);
        System.out.println("Sending... " + clientMessage);
        ServerResponse response = Utils.objectFromChannel(client, ServerResponse.class::cast);
        System.out.println("Response from server: " + response.output());
        if (input.equals("quit") && !response.failed()) {
          break;
        }
      }
    }
  }
}
