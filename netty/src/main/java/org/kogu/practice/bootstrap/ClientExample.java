package org.kogu.practice.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class ClientExample {
    private static final SimpleChannelInboundHandler<ByteBuf> handler =
            new SimpleChannelInboundHandler<ByteBuf>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                    System.out.println("Received Data");
                }
            };
    private static final InetSocketAddress address =
            new InetSocketAddress("www.manning.com", 80);

    private static final ChannelFutureListener listener = future -> {
        if (future.isSuccess()) {
            System.out.println("Connection established");
        } else {
            System.err.println("Connection attempt failed");
            future.cause().printStackTrace();
        }
    };

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(handler);

        bootstrap.connect(address)
                .addListener(listener);
    }
}
