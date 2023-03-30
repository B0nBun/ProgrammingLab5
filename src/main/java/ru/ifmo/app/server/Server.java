package ru.ifmo.app.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import ru.ifmo.app.lib.ClientMessage;
import ru.ifmo.app.lib.ServerResponse;
import ru.ifmo.app.lib.Utils;

public class Server {
  private static SelectionKey handleAccept(SocketChannel client, Selector selector)
      throws ClosedChannelException, IOException {
    client.configureBlocking(false);
    return client.register(selector, SelectionKey.OP_READ);
  }

  private static void handleRead(SocketChannel client) throws IOException, ClassNotFoundException {
    var message = Utils.objectFromChannel(client, ClientMessage.class::cast);
    System.out.println("Got the object");
    System.out.println("  " + message.message());
    System.out.println("  " + message.value());
    var buffer =
        Utils.objectToBuffer(ServerResponse.success("Got the message '" + message.message() + "'"));
    client.write(buffer);
    if (message.message().equals("quit")) {
      client.close();
    }
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    int port = 1111;
    InetSocketAddress addr = new InetSocketAddress("127.0.0.1", port);
    try (Selector selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();) {
      server.socket().bind(addr);
      server.configureBlocking(false);
      int ops = server.validOps();

      server.register(selector, ops, null);

      System.out.println("Server ready at " + addr);
      while (true) {
        selector.select();

        var keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
          var key = keys.next();
          if (key.isAcceptable()) {
            SocketChannel client = server.accept();
            Server.handleAccept(client, selector);
            System.out.println("Connection accepted: " + client.getLocalAddress());
          } else if (key.isReadable()) {
            var client = (SocketChannel) key.channel();
            Server.handleRead(client);
          }
          keys.remove();
        }
      }
    }
  }
}
