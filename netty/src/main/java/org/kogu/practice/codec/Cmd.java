package org.kogu.practice.codec;

import io.netty.buffer.ByteBuf;

public final class Cmd {
    private final ByteBuf name;
    private final ByteBuf args;
    public Cmd(ByteBuf name, ByteBuf args) {
        this.name = name;
        this.args = args;
    }
    public ByteBuf name() {
        return name;
    }
    public ByteBuf args() {
        return args;
    }
}