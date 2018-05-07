package org.kogu.practice.transmogrifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOBlockingServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ss = ServerSocketChannel.open();
        ss.bind(new InetSocketAddress(8080));
        while (true) {
            SocketChannel s = ss.accept();
            handle(s);
        }
    }

    private static void handle(SocketChannel s) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(80);
        try {
            int data;
            while ((data = s.read(buffer)) != -1) {
                buffer.flip();
                transmogrify(buffer);
                while (buffer.hasRemaining()) {
                    s.write(buffer);
                }
                buffer.compact();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void transmogrify(ByteBuffer buffer) {
        for (int i = 0; i < buffer.limit(); i++) {
            byte b = (byte) transmogrify(buffer.get(i));
            buffer.put(i, b);
        }
    }

    private static int transmogrify(int n) {
        return Character.isLetter(n) ? n ^ ' ' : n;
    }
}
