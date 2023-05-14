package ru.ifmo.app.client;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import ru.ifmo.app.shared.ClientRequest;
import ru.ifmo.app.shared.ServerResponse;
import ru.ifmo.app.shared.Utils;
import ru.ifmo.app.shared.commands.CommandParameters;

public class BlockingChannel implements AutoCloseable {

    SocketChannel channel;
    Selector selector;

    public BlockingChannel(SocketChannel channel) throws IOException {
        this.selector = Selector.open();
        channel.register(selector, channel.validOps());
        this.channel = channel;
    }

    public ServerResponse readResponse() throws IOException, ClassNotFoundException {
        while (true) {
            selector.select();
            var keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                var key = keys.next();
                keys.remove();

                if (key.isReadable()) {
                    ServerResponse response = Utils.objectFromChannel(
                        this.channel,
                        ServerResponse.class::cast
                    );
                    return response;
                }
            }
        }
    }

    public void writeRequest(ClientRequest<CommandParameters, Serializable> request)
        throws IOException {
        while (true) {
            selector.select();
            var keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                var key = keys.next();
                keys.remove();

                if (key.isWritable()) {
                    ByteBuffer buffer = Utils.objectToBuffer(request);
                    buffer.flip();
                    this.channel.write(buffer);
                    return;
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.selector.close();
        if (this.channel.isOpen()) this.channel.close();
    }
}
