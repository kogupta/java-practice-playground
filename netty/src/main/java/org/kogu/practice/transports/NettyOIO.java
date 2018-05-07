package org.kogu.practice.transports;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class NettyOIO {
  private static final byte[] hi = "Hi!\r\n".getBytes(StandardCharsets.UTF_8);
  private static final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(hi));

  public void serve(int port) throws IOException, InterruptedException {
    EventLoopGroup group = new OioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(group)
          .channel(OioServerSocketChannel.class)
          .childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
          .localAddress(new InetSocketAddress(port))
          .childHandler(new ChannelInit4());
      ChannelFuture future = b.bind().sync();
      future.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }

  private static final class ChannelInit4 extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
      ch.pipeline().addLast(new InbHandlerAdapter2());
    }
  }

  private static final class InbHandlerAdapter2 extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.writeAndFlush(buf.duplicate())
          .addListener(ChannelFutureListener.CLOSE);
    }
  }
}
