package org.kogu.practice.transmogrifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingIO {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8080);
        while (true) {
            Socket s = ss.accept();
            handle(s);
        }
    }

    private static void handle(Socket s) {
        try (InputStream in = s.getInputStream();
             OutputStream out = s.getOutputStream()) {
            int data;
            while ((data = in.read()) != -1) {
                int n = transmogrify(data);
                out.write(n);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static int transmogrify(int n) {
        return Character.isLetter(n) ? n ^ ' ' : n;
    }
}
