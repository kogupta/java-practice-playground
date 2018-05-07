package org.kogu.practice.misc;


/**
 * Yet another lambda util
 *  - checked exceptions from lambda expressions: bane of existence! arrghhhhhh
 */
public class LambdaUtil {

  /**
   * Works only with Java 8.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> RuntimeException rethrowSneaky(Throwable throwable) throws T {
    throw (T) throwable; // rely on vacuous cast
  }

  @FunctionalInterface
  public interface Function<T, R> {
    R apply(T t) throws Exception;

    static <T, R> java.util.function.Function<T, R> unchecked(Function<T, R> f) {
      return t -> {
        try {
          return f.apply(t);
        } catch (Exception e) {
          throw rethrowSneaky(e);
        }
      };
    }
  }

  public static <T, R> java.util.function.Function<T, R> unchecked(Function<T, R> f) {
    return Function.unchecked(f);
  }

  @FunctionalInterface
  public interface Predicate<T> {
    boolean test(T t) throws Exception;

    static <T> java.util.function.Predicate<T> unchecked(Predicate<T> p) {
      return t -> {
        try {
          return p.test(t);
        } catch (Exception e) {
          throw rethrowSneaky(e);
        }
      };
    }
  }

  public static <T> java.util.function.Predicate<T> unchecked(Predicate<T> p) {
    return Predicate.unchecked(p);
  }

  @FunctionalInterface
  public interface Consumer<T> {
    void accept(T t) throws Exception;

    static <T> java.util.function.Consumer<T> unchecked(Consumer<T> c) {
      return t -> {
        try {
          c.accept(t);
        } catch (Exception e) {
          throw rethrowSneaky(e);
        }
      };
    }
  }


  public static <T> java.util.function.Consumer<T> unchecked(Consumer<T> c) {
    return Consumer.unchecked(c);
  }
}