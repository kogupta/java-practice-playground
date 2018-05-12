package org.kogu.practice.timing.example;

import org.kogu.practice.timing.Timer;

import static org.kogu.practice.timing.Utils.sleep;

class Main {
  public static void main(String[] args) {
    Timer<Events> timer = Timer.initForEnum(Events.class);

    measure(timer);

    System.out.println(timer.log(false));

    System.out.println();
    System.out.println();

    System.out.println(timer.log(true));
  }

  private static void measure(Timer<Events> timer) {
    timer.start(Events.MetadataExtract);
    sleep(100);
    timer.stop(Events.MetadataExtract);

    timer.start(Events.RestCall);
    sleep(350);
    timer.stop(Events.RestCall);

    timer.start(Events.ResponseUnzip);
    sleep(50);
    timer.stop(Events.ResponseUnzip);

    timer.start(Events.JsonParse);
    sleep(150);
    timer.stop(Events.JsonParse);
  }

  private enum Events {
    MetadataExtract,
    RestCall,
    ResponseUnzip,
    JsonParse
  }
}
