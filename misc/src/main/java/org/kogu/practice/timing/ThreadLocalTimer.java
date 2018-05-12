package org.kogu.practice.timing;

public final class ThreadLocalTimer<T extends Enum<T>> implements Timer<T>{
  private static final String alreadyUnset = "No `Timer` stored in ThreadLocal - has it already been `unset`?";

  private final ThreadLocal<Timer<T>> tl;

  private ThreadLocalTimer(Class<T> clazz) {
    tl = new ThreadLocal<>();
    set(clazz);
  }

  private Timer<T> get() {
    Timer<T> timer = tl.get();
    if (timer != null) return timer;

    throw new AssertionError(alreadyUnset);
  }

  public void set(Class<T> clazz) {
    tl.remove();
    tl.set(Timer.initForEnum(clazz));
  }

  public void unset() {
    tl.remove();
  }

  @Override
  public void start(T key) {
    get().start(key);
  }

  @Override
  public void stop(T key) {
    get().stop(key);
  }

  @Override
  public void reset(T key) {
    get().reset(key);
  }

  @Override
  public long elapsedNanosForEvent(T key) {
    return get().elapsedNanosForEvent(key);
  }

  @Override
  public String log(boolean isOrderedByTime) {
    return get().log(isOrderedByTime);
  }

  public static <T extends Enum<T>> ThreadLocalTimer<T> initForEnum(Class<T> clazz) {
    return new ThreadLocalTimer<>(clazz);
  }
}
