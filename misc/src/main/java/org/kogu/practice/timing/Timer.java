package org.kogu.practice.timing;

import java.util.EnumSet;

public interface Timer<T extends Enum<T>> {
  void start(T key);

  void stop(T key);

  void reset(T key);

  long elapsedNanosForEvent(T key);

  String log(boolean isOrderedByTime);

  default String log() {
    return log(true);
  }

  static <T extends Enum<T>> Timer<T> initForEnum(Class<T> elementType) {
    return new SingleThreadedTimer<>(EnumSet.allOf(elementType), elementType);
  }
}
