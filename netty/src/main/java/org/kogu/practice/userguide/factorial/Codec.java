package org.kogu.practice.userguide.factorial;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToByteEncoder;

import java.math.BigInteger;
import java.util.List;

class Codec {
  public static final class NumberEncoder extends MessageToByteEncoder<Number> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Number msg, ByteBuf out) {
      BigInteger n = msg instanceof BigInteger ? (BigInteger) msg : new BigInteger(String.valueOf(msg));
      byte[] bytes = n.toByteArray();

      out.writeByte((byte) 'F');
      out.writeInt(bytes.length);
      out.writeBytes(bytes);
    }
  }

  public static final class BigIntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
      // Wait until the length prefix is available
      if (in.readableBytes() < 5) {
        return;
      }

      in.markReaderIndex();

      // Check the magic number.
      int magicNumber = in.readUnsignedByte();
      if (magicNumber != 'F') {
        in.resetReaderIndex();
        throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
      }

      // Wait until the whole data is available.
      int dataLength = in.readInt();
      if (in.readableBytes() < dataLength) {
        in.resetReaderIndex();
        return;
      }

      // Convert the received data into a new BigInteger.
      byte[] decoded = new byte[dataLength];
      in.readBytes(decoded);

      out.add(new BigInteger(decoded));
    }
  }
}
