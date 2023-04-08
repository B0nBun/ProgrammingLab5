package ru.ifmo.app.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import ru.ifmo.app.lib.ClientMessage;
import ru.ifmo.app.lib.ServerResponse;
import ru.ifmo.app.lib.Utils;

// TODO: Ctrl+C on client causes BufferUnderflowException
public class Client {
  public static void main(String[] args)
      throws IOException, ClassNotFoundException, InterruptedException {
    int port = 1111;
    InetSocketAddress addr = new InetSocketAddress("127.0.0.1", port);

    try (SocketChannel client = SocketChannel.open(addr);
        Selector selector = Selector.open();
        Scanner scanner = new Scanner(System.in);) {

      client.configureBlocking(false);
      int ops = client.validOps();
      client.register(selector, ops);
      System.out.println("Connected to " + addr + "...");

      while (true) {
        System.out.print("Input the message for the server: ");
        var messageContent = scanner.nextLine();
        boolean eventLoop = true;

        while (eventLoop) {
          selector.select();
          var keys = selector.selectedKeys().iterator();
          while (keys.hasNext()) {
            var key = keys.next();
            keys.remove();

            if (key.isWritable()) {
              var clientMessage = ClientMessage.withoutParams("help");
              ByteBuffer buffer = Utils.objectToBuffer(clientMessage);
              buffer.flip();
              client.write(buffer);
              System.out.println("Sending: " + clientMessage);
              key.interestOps(SelectionKey.OP_READ);
            } else if (key.isReadable()) {
              ServerResponse response = Utils.objectFromChannel(client, ServerResponse.class::cast);
              System.out.println("Got response from the server: " + response.output());
              key.interestOps(SelectionKey.OP_WRITE);
              eventLoop = false;
            }
          }
        }
      }
    }
  }
}
