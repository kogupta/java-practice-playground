package org.kogu.practice.userguide;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ThreadFactory;

public class TimeServer {
    private final int port;

    private TimeServer(int port) {
        this.port = port;
    }

    private void run() throws Exception {
        ThreadFactory bossTF = new ThreadFactoryBuilder().setNameFormat("boss-%d").build();
        EventLoopGroup boss = new NioEventLoopGroup(1, bossTF);

        ThreadFactory workerTF = new ThreadFactoryBuilder().setNameFormat("worker-%d").build();
        EventLoopGroup workers = new NioEventLoopGroup(2, workerTF);

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
            ch.pipeline().addLast("unix-time", new TimeServerHandler());
        }
    };

    private static final class TimeServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(Thread.currentThread().getName() + " -> channel read");
            ByteBuf in = ctx.alloc().buffer(Integer.BYTES);
            in.writeInt((int) (System.currentTimeMillis() / 1_000L + 2_208_988_800L));
            ctx.writeAndFlush(in).addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            System.out.println(Thread.currentThread().getName() + " -> channel exception caught");
            cause.printStackTrace();
            ctx.close();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(Thread.currentThread().getName() + " -> channel inactive");
            ctx.close();
        }
    }

    public static void main(String[] args) throws Exception {
        new TimeServer(8888).run();
    }
}
