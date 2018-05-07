package org.kogu.practice.functions;

import java.nio.ByteBuffer;
import java.util.function.Function;

public interface PascalCodec {
  public static void write(byte[] value, ByteBuffer target, int index) {
    target.putInt(index, value.length);
    target.put(value, index + Integer.BYTES, value.length);
  }

  public static <T> T read(ByteBuffer src, int index, Function<byte[], T> fn) {
    int len = src.getInt(index);
    byte[] bytes = new byte[len];
    src.get(bytes, index + Integer.BYTES, len);
    return fn.apply(bytes);
  }
}
