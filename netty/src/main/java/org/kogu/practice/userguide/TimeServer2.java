package org.kogu.practice.userguide;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.concurrent.ThreadFactory;

public class TimeServer2 {
    private final int port;

    private TimeServer2(int port) {
        this.port = port;
    }

    private void run() throws Exception {
        ThreadFactory bossTF = new ThreadFactoryBuilder().setNameFormat("boss-%d").build();
        EventLoopGroup boss = new NioEventLoopGroup(1, bossTF);

        ThreadFactory workerTF = new ThreadFactoryBuilder().setNameFormat("worker-%d").build();
        EventLoopGroup workers = new NioEventLoopGroup(1, workerTF);

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, workers)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(initializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }

    private static final ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) {
            System.out.println(Thread.currentThread().getName() + " -> init channel");
            ch.pipeline().addLast(new UnixTimeEncoder(), new TimeServerHandler());
        }
    };

    private static final class UnixTimeEncoder extends MessageToByteEncoder<UnixTime> {
        @Override
        protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) {
            System.out.println(Thread.currentThread().getName() + " -> encoding aka obj => byte[]");
            out.writeInt((int) msg.value());
        }
    }

    private static final class TimeServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println(Thread.currentThread().getName() + " -> channel read");
            ctx.writeAndFlush(new UnixTime())
                    .addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            System.out.println(Thread.currentThread().getName() + " -> channel exception caught");
            cause.printStackTrace();
            ctx.close();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            System.out.println(Thread.currentThread().getName() + " -> channel inactive");
            ctx.close();
        }
    }

    public static void main(String[] args) throws Exception {
        new TimeServer2(8888).run();
    }
}
