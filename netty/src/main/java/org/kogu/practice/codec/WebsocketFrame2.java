package org.kogu.practice.codec;

import io.netty.buffer.ByteBuf;

public class WebsocketFrame2 {
    public enum FrameType{
        PING,
        PONG,
        TEXT,
        BINARY,
        CLOSE,
        CONTINUATION
    }

    private final FrameType type;
    private final ByteBuf buf;

    public WebsocketFrame2(FrameType type, ByteBuf buf) {
        this.type = type;
        this.buf = buf;
    }

    public FrameType getType() {
        return type;
    }

    public ByteBuf getBuf() {
        return buf;
    }
}
