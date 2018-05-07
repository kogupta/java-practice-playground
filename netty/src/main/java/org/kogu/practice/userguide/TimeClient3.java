package org.kogu.practice.userguide;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class TimeClient3 {
    private static final ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new TimeDecoder(), new TimeClientHandler());
        }
    };

    private static final class TimeDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (in.readableBytes() < 4) {
                return;
            }

            out.add(new UnixTime(in.readUnsignedInt()));
        }
    }

    private static final class TimeClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            UnixTime m = (UnixTime) msg; // (1)
            System.out.println(m.toString());
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static void main(String[] args) throws Exception {
        EventLoopGroup workers = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();
        try {
            b.group(workers)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(initializer);
            ChannelFuture future = b.connect("localhost", 8888).sync();
            future.channel().closeFuture().sync();
        } finally {
            workers.shutdownGracefully();
        }
    }
}
