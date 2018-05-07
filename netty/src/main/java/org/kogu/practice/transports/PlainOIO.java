package org.kogu.practice.transports;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PlainOIO {
  private static final byte[] hi = "Hi!\r\n".getBytes(StandardCharsets.UTF_8);

  public void serve(int port) throws IOException {
    ServerSocket socket = new ServerSocket(port);
    try {
      while (true) {
        Socket clientSocket = socket.accept();
        System.out.println("Accepted connection from client socket: " + clientSocket);
        new Thread(() -> {
          try (OutputStream out = clientSocket.getOutputStream()) {
            out.write(hi);
            out.flush();
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            try {
              clientSocket.close();
            } catch (IOException ignore) {}
          }
        }).start();
      }
    } catch (IOException e){
      e.printStackTrace();
    }
  }
}
