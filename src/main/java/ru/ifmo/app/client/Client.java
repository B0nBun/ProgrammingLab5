package ru.ifmo.app.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import ru.ifmo.app.client.exceptions.CommandParseException;
import ru.ifmo.app.server.exceptions.InvalidCommandParametersException;
import ru.ifmo.app.shared.ClientRequest;
import ru.ifmo.app.shared.Command;
import ru.ifmo.app.shared.CommandRegistery;
import ru.ifmo.app.shared.ServerResponse;
import ru.ifmo.app.shared.Utils;

// TODO: Ctrl+C on client causes BufferUnderflowException
public class Client {
  private static ClientRequest<Serializable> requestFromCommandString(String commandString) throws CommandParseException, InvalidCommandParametersException {
    var splitted = Arrays.asList(commandString.trim().split("\s+"));
    if (splitted.size() == 0) {
      throw new CommandParseException("Expected a command, but got nothing");
    }
    String commandName = splitted.get(0);
    List<String> commandArguments = splitted.subList(1, splitted.size());
    Command command = CommandRegistery.global.get(commandName);
    if (command == null) {
      throw new CommandParseException("Command not found");
    }
    Serializable params = command.parametersObjectFromStrings(commandArguments.toArray(new String[commandArguments.size()]));
    return new ClientRequest<>(commandName, params);
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException,
      InterruptedException, CommandParseException, InvalidCommandParametersException {
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
        var commandString = scanner.nextLine();
        var requestObject = Client.requestFromCommandString(commandString);

        // TODO: Add timeout to avoid infinite loop
        handleLoop: while (true) {
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
              ServerResponse response = Utils.objectFromChannel(client, ServerResponse.class::cast);
              System.out.println("Response: \n" + response.output());
              key.interestOps(SelectionKey.OP_WRITE);
              break handleLoop;
            }
          }
        }
      }
    }
  }
}
