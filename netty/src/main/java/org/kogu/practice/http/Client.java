package org.kogu.practice.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;
import static io.netty.handler.codec.http.HttpHeaderNames.HOST;

public class Client {
  private static final int maxContentLength = 20 * 1024 * 1024; // 20 MB

  private final EventLoopGroup eventLoop;
  private final Bootstrap bootstrap;

  private Client(EventLoopGroup eventLoop) {
    this.eventLoop = eventLoop;
    this.bootstrap = new Bootstrap();
    bootstrap.group(eventLoop)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .handler(new ClientInit());
  }

  public void shutdown() {
    eventLoop.shutdownGracefully();
  }

  public ChannelFuture executeGet(URI uri, BasicAuthString auth) {
    try {
      Channel ch = bootstrap.connect(uri.getHost(), uri.getPort())
          .sync()
          .channel();
      HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
      request.headers().set(HOST, uri.getHost());
      request.headers().set(AUTHORIZATION, auth.authString);

      // Send the HTTP request.
      return ch.writeAndFlush(request);
    } catch (InterruptedException e) {
      throw rethrow(e);
    }
  }

  /**
   * Copied from: https://stackoverflow.com/a/4555351
   * Cast a CheckedException as an unchecked one.
   *
   * @param throwable to cast
   * @param <T> the type of the Throwable
   * @return this method will never return a Throwable instance, it will just throw it.
   * @throws T the throwable as an unchecked throwable
   */
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> RuntimeException rethrow(Throwable throwable) throws T {
    throw (T) throwable; // rely on vacuous cast
  }

  public static final class BasicAuthString {
    public final String authString;

    public BasicAuthString(String s) {this.authString = s;}

    public static BasicAuthString create(String username, String password) {
      String authString = username + ":" + password;
      ByteBuf byteBuf = ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, authString);
      ByteBuf encoded = Base64.encode(byteBuf, false);
      String s = "Basic " + encoded.toString(CharsetUtil.UTF_8);
      return new BasicAuthString(s);
    }
  }

  public static Client newInstance() {
    return new Client(new NioEventLoopGroup());
  }
  public static Client newInstance(@NotNull EventLoopGroup eventLoop) {
    return new Client(eventLoop);
  }

  private static final class ClientInit extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
      ChannelPipeline p = ch.pipeline();
      p.addLast(new HttpClientCodec());
      p.addLast(new HttpObjectAggregator(maxContentLength));
      p.addLast(new CustomHandler2());
    }
  }

  private static final class CustomHandler2 extends SimpleChannelInboundHandler<FullHttpResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) {
      ByteBuf buf = response.content();
      int len = buf.readableBytes();

      System.out.println(" -----------------------");
      System.out.println(this);
      System.out.println(response);
      System.err.println("STATUS: " + response.status());
      System.err.println("VERSION: " + response.protocolVersion());
      printHeaders(response);
      System.out.println("response size: " + len);

      InputStream is = new ByteBufInputStream(buf, len);
      try {
        long t0 = System.currentTimeMillis();
        Client2.blah(is);
        System.out.println("Time taken to parse: " + (System.currentTimeMillis() - t0) + " millis");
        System.out.println(" -----------------------");
      } catch (IOException e) {
        throw rethrow(e);
      }
    }

    private void printHeaders(FullHttpResponse response) {
      if (!response.headers().isEmpty()) {
        for (CharSequence name : response.headers().names()) {
          for (CharSequence value : response.headers().getAll(name)) {
            System.err.println("HEADER: " + name + " = " + value);
          }
        }
        System.err.println();
      }
    }
  }

  public static final class ByteBufInputStream extends InputStream {
    private ByteBuf buf;
    private int len;

    public ByteBufInputStream( ByteBuf buf, int len ) {
      this.buf = buf;
      this.len = len;
    }

    @Override public int available() { return this.len;}

    @Override public void close() {}

    @Override public void mark(int readlimit) { throw new RuntimeException( "not supported");}

    @Override public void reset() { throw new RuntimeException( "not supported");}

    @Override public boolean markSupported() { return false;}

    @Override
    public int read() throws IOException {
      ensureBytesAvailable( 1 );
      this.len --;
      return this.buf.readByte();

    }

    @Override public int read(byte[] b) throws IOException { return read( b, 0, b.length );}

    @Override
    public int read(byte[] b, int offset, int len) throws IOException {
      ensureBytesAvailable( len );
      this.len -= len;
      this.buf.readBytes(b, offset, len );
      return len;
    }

    @Override
    public long skip(long n) throws IOException {
      ensureBytesAvailable( (int)n );
      len -= n;
      return n;
    }

    private void ensureBytesAvailable( int n ) throws IOException {
      if( n > this.len ) throw new IOException( "available bytes is less than " + n );
    }
  }

  public static void main(String[] args) throws InterruptedException {
    NioEventLoopGroup eventLoop = new NioEventLoopGroup();
    Client client = Client.newInstance(eventLoop);
    URI uri = testURI();
    BasicAuthString authString = BasicAuthString.create("weblogic", "welcome1");
    for (int i = 0; i < 10; i++) {
      client.executeGet(uri, authString);
    }
    TimeUnit.SECONDS.sleep(30);
    client.shutdown();
  }

  @NotNull
  private static URI testURI() {
    // note - this url may not be working currently
    String host = "slc11fqy.us.oracle.com";
    int port = 7010;
    String path = "emaas/dataservice/reader/api/v1/reader/emcsas_sma_la_data/17560/98db6484078a4a27a4aae0941d568d07";

    String url = String.format("http://%s:%d/%s", host, port, path);
    try {
      return new URI(url);
    } catch (URISyntaxException e) {
      throw rethrow(e);
    }
  }

//  private static final class ConnectionListener implements ChannelFutureListener {
//    @Override
//    public void operationComplete(ChannelFuture future) {
//      if (future.isSuccess()) {
//        return;
//      }
//
//      System.out.println("reconnect ..... ");
//      EventLoop eventLoop = future.channel().eventLoop();
//      Runnable command = () -> {
//
//      };
//      eventLoop.schedule(command, 1, TimeUnit.SECONDS);
//    }
//  }
}
