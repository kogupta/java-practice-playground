package org.kogu.practice.http;

import org.asynchttpclient.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.basicAuthRealm;

public class Client2 {
  public static void main(String[] args) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
    String username = "weblogic";
    String password = "welcome1";
    URI uri = uri();

    try (AsyncHttpClient asyncHttpClient = asyncHttpClient()) {
      Request req = new RequestBuilder()
          .setMethod("GET")
          .setUrl(uri.toASCIIString())
          .setRealm(basicAuthRealm(username, password))
          .build();
      AsyncCompletionHandler<String> handler = new AsyncCompletionHandler<String>() {
        @Override
        public String onCompleted(Response response) throws Exception {
          byte[] bytes = response.getResponseBodyAsBytes();
          System.out.println("Response size: " + bytes.length);
          String s = "/tmp/response";
          Files.write(Paths.get(s), bytes, CREATE, APPEND);
          return s;
        }
      };

      ListenableFuture<String> ft = asyncHttpClient
          .executeRequest(req, handler)
          .addListener(() -> System.out.println("Written to file ...."), null);

      System.out.println(ft.get());
    }
  }

  @NotNull
  public static URI uri() throws URISyntaxException {
    String host = "slc11fqy.us.oracle.com";
    int port = 7010;
    String path = "emaas/dataservice/reader/api/v1/reader/emcsas_sma_la_data/17560/98db6484078a4a27a4aae0941d568d07";

    String url = String.format("http://%s:%d/%s", host, port, path);
    return new URI(url);
  }

  public static void blah(InputStream is) throws IOException {
    PersistStoreInputStream psi = new PersistStoreInputStream(is);
    PersistStoreStream.PersistStoreEntry entry;
    while ((entry = psi.getNextEntry()) != null) {
      InputStream eis = null;
      try {
        eis = entry.getInputStream();
        String str = toString(eis);
        Files.write(Paths.get("/tmp/response_parsed"), str.getBytes(), CREATE, WRITE, APPEND);
        List<Map> jsonList = JsonConverter.listFromJson(Map.class, str);
        System.out.println("Number of items obtained: " + jsonList.size());
      } finally {
        if (eis != null)
          eis.close();
      }
    }
  }

  private static String toString(InputStream in) {
    String newLine = System.getProperty("line.separator");

    try (InputStreamReader r = new InputStreamReader(in);
         BufferedReader br = new BufferedReader(r)) {
      return br.lines().reduce("", (a, b) -> a + b + newLine, (a, b) -> a + b);
    } catch (IOException t) {
      throw new UncheckedIOException(t);
    }
  }
}
