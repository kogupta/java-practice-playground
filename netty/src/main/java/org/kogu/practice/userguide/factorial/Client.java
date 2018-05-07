package org.kogu.practice.userguide.factorial;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
  private static final int count = 1_000;
  public static void main(String[] args) throws InterruptedException {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group)
          .channel(NioSocketChannel.class)
          .handler(new FactorialClientInitializer());

      // Make a new connection.
      ChannelFuture f = b.connect("127.0.0.1", 8088).sync();

      // Get the handler instance to retrieve the answer.
      FactorialClientHandler handler =
          (FactorialClientHandler) f.channel().pipeline().last();

      // Print out the answer.
      System.err.format("Factorial of %,d is: %,d", count, handler.getFactorial());
    } finally {
      group.shutdownGracefully();
    }
  }

  private static final class FactorialClientHandler extends SimpleChannelInboundHandler<BigInteger> {
    private ChannelHandlerContext ctx;
    private int receivedMessages;
    private int next = 1;
    final BlockingQueue<BigInteger> answer = new LinkedBlockingQueue<>();

    public BigInteger getFactorial() {
      boolean interrupted = false;
      try {
        for (;;) {
          try {
            return answer.take();
          } catch (InterruptedException ignore) {
            interrupted = true;
          }
        }
      } finally {
        if (interrupted) {
          Thread.currentThread().interrupt();
        }
      }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
      this.ctx = ctx;
      sendNumbers();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, final BigInteger msg) {
      receivedMessages ++;
      if (receivedMessages == count) {
        // Offer the answer after closing the connection.
        ctx.channel().close().addListener((ChannelFutureListener) future -> {
          boolean offered = answer.offer(msg);
          assert offered;
        });
      }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
      ctx.close();
    }

    private void sendNumbers() {
      // Do not send more than 4096 numbers.
      ChannelFuture future = null;
      for (int i = 0; i < 4096 && next <= count; i++) {
        future = ctx.write(next);
        next++;
      }
      if (next <= count) {
        assert future != null;
        future.addListener(numberSender);
      }
      ctx.flush();
    }

    private final ChannelFutureListener numberSender = future -> {
      if (future.isSuccess()) {
        sendNumbers();
      } else {
        future.cause().printStackTrace();
        future.channel().close();
      }
    };
  }

  private static final class FactorialClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
      ChannelPipeline pipeline = ch.pipeline();

      pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
      pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

      pipeline.addLast(new Codec.BigIntegerDecoder());
      pipeline.addLast(new Codec.NumberEncoder());

      pipeline.addLast(new FactorialClientHandler());
    }
  }
}
