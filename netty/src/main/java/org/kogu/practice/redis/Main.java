package org.kogu.practice.redis;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class Main {
  private static final String host = "127.0.0.1";
  private static final int port = 6379;

  public static void main(String[] args) throws InterruptedException, IOException {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.SO_KEEPALIVE, true)
          .handler(new RedisClientInitializer());

      // start connection attempt
      Channel channel = b.connect(host, port).sync().channel();

      // Read commands from the stdin.
      System.out.println("Enter Redis commands (quit to end)");
      ChannelFuture lastWriteFuture = null;
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      for (;;) {
        final String input = in.readLine();
        final String line = input != null ? input.trim() : null;
        if (line == null || "quit".equalsIgnoreCase(line)) { // EOF or "quit"
          channel.close().sync();
          break;
        } else if (line.isEmpty()) { // skip `enter` or `enter` with spaces.
          continue;
        }
        // Sends the received line to the server.
        lastWriteFuture = channel.writeAndFlush(line);
        lastWriteFuture.addListener(future -> {
          if (!future.isSuccess()) {
            System.err.print("write failed: ");
            future.cause().printStackTrace(System.err);
          }
        });
      }

      // Wait until all messages are flushed before closing the channel.
      if (lastWriteFuture != null) {
        lastWriteFuture.sync();
      }
    } finally {
      group.shutdownGracefully();
    }
  }

  public static final class RedisClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
      ChannelPipeline p = ch.pipeline();
      p.addLast(new RedisEncoder());
      p.addLast(new RedisDecoder());
      p.addLast(new RedisBulkStringAggregator());
      p.addLast(new RedisArrayAggregator());
      p.addLast(new RedisClientHandler());
    }
  }

  public static final class RedisClientHandler extends ChannelDuplexHandler {
    private static final Pattern p = Pattern.compile("\\s+");

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
      String[] commands = p.split((String) msg, 0);
      System.out.println("Commands: " + Arrays.toString(commands));

      List<RedisMessage> children = new ArrayList<>(commands.length);
      for (String command : commands) {
        ByteBuf byteBuf = ByteBufUtil.writeUtf8(ctx.alloc(), command);
        FullBulkStringRedisMessage message = new FullBulkStringRedisMessage(byteBuf);
        children.add(message);
      }

      RedisMessage message = new ArrayRedisMessage(children);
      ctx.write(message, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
      RedisMessage redisMessage = (RedisMessage) msg;
      printAggregatedRedisResponse(redisMessage);
      ReferenceCountUtil.release(redisMessage);
    }

    private void printAggregatedRedisResponse(RedisMessage msg) {
      if (msg instanceof SimpleStringRedisMessage) {
        System.out.println(((SimpleStringRedisMessage) msg).content());
      } else if (msg instanceof ErrorRedisMessage) {
        System.out.println(((ErrorRedisMessage) msg).content());
      } else if (msg instanceof IntegerRedisMessage) {
        System.out.println(((IntegerRedisMessage) msg).value());
      } else if (msg instanceof FullBulkStringRedisMessage) {
        System.out.println(getString((FullBulkStringRedisMessage) msg));
      } else if (msg instanceof ArrayRedisMessage) {
        for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
          printAggregatedRedisResponse(child);
        }
      } else {
        throw new CodecException("unknown message type: " + msg);
      }
    }

    private String getString(FullBulkStringRedisMessage msg) {
      return msg.isNull() ? "(null)" : msg.content().toString(CharsetUtil.UTF_8);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      System.err.print("exceptionCaught: ");
      cause.printStackTrace(System.err);
      ctx.close();
    }
  }
}
