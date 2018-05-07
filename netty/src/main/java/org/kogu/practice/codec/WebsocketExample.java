package org.kogu.practice.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.*;

import java.util.List;

public class WebsocketExample extends MessageToMessageCodec<WebSocketFrame, WebsocketFrame2> {

    @Override
    protected void encode(ChannelHandlerContext ctx, WebsocketFrame2 msg, List<Object> out) throws Exception {
        ByteBuf buf = msg.getBuf().retainedDuplicate();
        switch (msg.getType()) {
            case PING:
                out.add(new PingWebSocketFrame(buf));
                break;
            case PONG:
                out.add(new PongWebSocketFrame(buf));
                break;
            case TEXT:
                out.add(new TextWebSocketFrame(buf));
                break;
            case BINARY:
                out.add(new BinaryWebSocketFrame(buf));
                break;
            case CLOSE:
                out.add(new CloseWebSocketFrame(true, 0, buf));
                break;
            case CONTINUATION:
                out.add(new ContinuationWebSocketFrame(buf));
                break;
            default:
                throw new IllegalStateException("Unsupported Websocket message: " + msg);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {

    }
}
