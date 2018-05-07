package org.kogu.practice.transports;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class PlainNIO {
  private static final ByteBuffer hi = ByteBuffer.wrap("Hi!\r\n".getBytes(StandardCharsets.UTF_8));

  public void serve(int port) throws IOException {
    ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.configureBlocking(false);
    ServerSocket serverSocket = serverChannel.socket();
    serverSocket.bind(new InetSocketAddress(port));
    Selector selector = Selector.open();
    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    while (true) {
      try {
        selector.select();
      } catch (IOException e) {
        break;
      }

      Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        iterator.remove();
        try {
          if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, hi.duplicate());
            System.out.println("Accepted connection from " + client);
          }
          if (key.isWritable()) {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            while (buffer.hasRemaining()) {
              if (client.write(buffer) == 0) {
                break;
              }
            }
            client.close();
          }
        } catch (IOException e) {
          key.cancel();
          try {
            key.channel().close();
          } catch (IOException ignore) {}
        }
      }
    }
  }

}
