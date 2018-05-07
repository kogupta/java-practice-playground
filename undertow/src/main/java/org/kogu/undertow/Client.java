package org.kogu.undertow;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Client {
  public static void main(String[] args) throws IOException {
    OkHttpClient client = new OkHttpClient();
    String url = "http://localhost:8080/consumer";

    Request request = new Request.Builder()
        .url(url)
        .build();

    try (Response response = client.newCall(request).execute()) {
      System.out.println(response.body().string());
    }
  }
}
