package org.kogu.practice.echo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.atomic.AtomicLong;

public class ByteCounter extends SimpleChannelInboundHandler<ByteBuf> {
    private final String id;
    private final AtomicLong readBytes;
    private final AtomicLong writtenBytes;

    public ByteCounter(String id) {
        this.id = id;
        readBytes = new AtomicLong();
        writtenBytes = new AtomicLong();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

    }

    public long bytesRead() {
        return readBytes.get();
    }

    public long bytesWritten() {
        return writtenBytes.get();
    }
}
