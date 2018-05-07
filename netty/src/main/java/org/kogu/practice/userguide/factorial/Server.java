package org.kogu.practice.userguide.factorial;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.NettyRuntime;

import java.math.BigInteger;
import java.util.concurrent.ThreadFactory;

public class Server {
  private static final ThreadFactory bossTF = new ThreadFactoryBuilder()
      .setNameFormat("boss-%d")
      .setDaemon(true)
      .build();
  private static final ThreadFactory workerTF = new ThreadFactoryBuilder()
      .setNameFormat("worker-%d")
      .setDaemon(true)
      .build();

  public static void main(String[] args) throws InterruptedException {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1, bossTF);
    EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors() * 2, workerTF);

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new FactorialServerInitializer());

      b.bind(8088)
          .sync()
          .channel()
          .closeFuture()
          .sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  private static final class FactorialServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
      ChannelPipeline pipeline = ch.pipeline();

      pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
      pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

      pipeline.addLast(new Codec.BigIntegerDecoder());
      pipeline.addLast(new Codec.NumberEncoder());

      pipeline.addLast(new FactorialServerHandler());
    }
  }

  private static final class FactorialServerHandler extends SimpleChannelInboundHandler<BigInteger> {
    private BigInteger lastMultiplier = BigInteger.ONE;
    private BigInteger factorial = BigInteger.ONE;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BigInteger msg) {
      // Calculate the cumulative factorial and send it to the client.
      lastMultiplier = msg;
      factorial = factorial.multiply(msg);
      ctx.writeAndFlush(factorial);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
      System.err.printf("Factorial of %,d is: %,d%n", lastMultiplier, factorial);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
      ctx.close();
    }
  }
}
