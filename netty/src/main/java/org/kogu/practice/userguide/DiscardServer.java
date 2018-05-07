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

public class DiscardServer {
    private final int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
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
            ch.pipeline().addLast("discard", new DiscardServerHandler());
        }
    };

    private static final class DiscardServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println(Thread.currentThread().getName() + " -> channel read");
            ByteBuf in = (ByteBuf) msg;
            try {
                System.out.println(in.toString(CharsetUtil.ISO_8859_1));
            } finally {
                ReferenceCountUtil.release(msg);
//                in.release();
            }
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
        new DiscardServer(8888).run();
    }
}
