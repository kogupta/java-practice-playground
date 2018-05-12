package org.kogu.practice.timing.example;

import org.kogu.practice.timing.ThreadLocalTimer;
import org.kogu.practice.timing.Timer;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.kogu.practice.timing.Utils.line;
import static org.kogu.practice.timing.Utils.sleep;

class ThreadLocalApiExample implements Runnable {
  private final ThreadLocalTimer<Events> tlTimer;
  private static final Random r = new Random(31012010);

  public ThreadLocalApiExample() {
    tlTimer = ThreadLocalTimer.initForEnum(Events.class);
  }

  public void importantSeriesOfSteps() {
    tlTimer.set(Events.class);
    m1();
    m2();

    System.out.println(tlTimer.log(false));
  }

  public void otherCode() {
    // wont compile -> `tlTimer` is bound to `Events` enum -
    // need to create another ThreadLocal; or,
    // directly use the `Timer` class
//    tlTimer.set(OtherEvents.class);
    Timer<OtherEvents> timer = Timer.initForEnum(OtherEvents.class);
    timer.start(OtherEvents.Unzip);
    m1();
    timer.stop(OtherEvents.Unzip);
    timer.start(OtherEvents.Serialize);
    m2();
    timer.stop(OtherEvents.Serialize);

    System.out.println(timer.log(false));
  }

  private void m1() {
    tlTimer.start(Events.MetadataExtract);
    importantOldCode();
    tlTimer.stop(Events.MetadataExtract);

    tlTimer.start(Events.RestCall);
    importantOldCode();
    tlTimer.stop(Events.RestCall);
  }

  private void m2() {
    tlTimer.start(Events.JsonParse);
    importantOldCode();
    tlTimer.stop(Events.JsonParse);
  }

  private static void importantOldCode() {
    int millis = 100 + r.nextInt(100);
    sleep(millis);
  }

  @Override
  public void run() {
    String line = line(20, '=');
    for (int i = 0; i < 4; i++) {
      System.out.println("Iteration: " + i);
      System.out.println(line);
      importantSeriesOfSteps();
    }
  }

  private enum Events {
    MetadataExtract,
    RestCall,
    JsonParse,
    ;
  }

  private enum OtherEvents {
    Unzip,
    Serialize,
    Deserialize,
    ;
  }

  public static void main(String[] args) {
    ThreadLocalApiExample example = new ThreadLocalApiExample();
    example.importantSeriesOfSteps();

    System.out.println(line(50, '-'));
    ExecutorService es = Executors.newSingleThreadExecutor();
    es.execute(example);

    sleep(10, TimeUnit.SECONDS);
    es.shutdown();
    System.out.println(line(50, '-'));

    example.importantSeriesOfSteps();
    System.out.println();

    example.otherCode();
  }

}