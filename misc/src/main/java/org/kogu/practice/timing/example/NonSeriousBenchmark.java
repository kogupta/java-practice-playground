package org.kogu.practice.timing.example;

import com.google.common.base.Stopwatch;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import org.kogu.practice.timing.Timer;

import java.io.IOException;

import static org.kogu.practice.timing.Utils.sleep;

// jvm benchmarks w/o JMH => not serious
class NonSeriousBenchmark {
  private static final int warmup = 1_000;
  private static final Stopwatch sw = Stopwatch.createUnstarted();
  private static String init;
  private static String displayEvents;
  private static String displayOrdered;

  public static void main(String[] args) {
    for (int i = -warmup; i < 0; i++) {
      boolean print = i % 100 == 0;
      iterate(print);
      sw.reset();
    }

    iterate(true);
  }

  private static void iterate(boolean print) {
    sw.start();
    Timer<Events> timer = Timer.initForEnum(Events.class);
    sw.stop();
    if (print) init = sw.toString();

    measure(timer);

    sw.reset().start();
    String s = timer.log(false);
    sw.stop();
    ignore(s);
    if (print) displayEvents = sw.toString();

    sw.reset().start();
    s = timer.log();
    sw.stop();
    ignore(s);
    if (print) displayOrdered = sw.toString();

    if (print)
      System.out.printf("Init - event order - time order => %s - %s - %s%n", init, displayEvents, displayOrdered);
  }

  private static void measure(Timer<Events> timer) {
    timer.start(Events.EventOne);
    sleep(100);
    timer.stop(Events.EventOne);

    timer.start(Events.EventTwo);
    sleep(50);
    timer.stop(Events.EventTwo);
  }

  private static void ignore(String s) {
    try {
      // haha
      CharStreams.nullWriter().write(s);
    } catch (IOException ignored) {}
  }

  private enum Events {
    EventOne,
    EventTwo,
  }
}
