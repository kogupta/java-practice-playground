package org.kogu.practice.websockets;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

public class ChatServer {
    private final ChannelGroup channels = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer(channels));

        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly();
        channel = future.channel();
        return future;

    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channels.close();
        eventLoopGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(8888);
        ChatServer server = new ChatServer();
        ChannelFuture future = server.start(address);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.destroy();
        }));

        future.channel().closeFuture().syncUninterruptibly();
    }

    private static final class ServerInitializer extends ChannelInitializer<Channel> {
        private final ChannelGroup group;

        public ServerInitializer(ChannelGroup group) {
            this.group = group;
        }

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
            pipeline.addLast(new HttpRequestHandler("/ws"));
            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
            pipeline.addLast(new TextWSFrameHandler(group));
        }
    }
}