package org.kogu.practice.timing;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

import java.util.*;

import static java.util.concurrent.TimeUnit.*;

final class SingleThreadedTimer<T extends Enum<T>> implements Timer<T> {
  private final EnumMap<T, Stopwatch> events;
  private final Class<T> eventType;

  SingleThreadedTimer(EnumSet<T> keys, Class<T> type) {
    Preconditions.checkArgument(keys != null && keys.size() > 0);
    Preconditions.checkNotNull(type);

    eventType = type;
    events = new EnumMap<>(type);
    for (T key : keys)
      events.put(key, Stopwatch.createUnstarted());
  }

  public void start(T key) { events.get(key).start();}
  public void stop(T key) { events.get(key).stop();}
  public void reset(T key) { events.get(key).reset();}

  @Override
  public long elapsedNanosForEvent(T key) {
    return events.get(key).elapsed(NANOSECONDS);
  }

  public String log(boolean isOrderedByTime) {
    int capacity = events.size() * 20 + defaultLineLength * 3;

    StringBuilder sb = new StringBuilder(isOrderedByTime ? capacity * 2 : capacity);

    if (isOrderedByTime) {
      _log(true, sb);
      sb.append(System.lineSeparator()).append(System.lineSeparator());
    }

    _log(false, sb);
    return sb.toString();
  }

  private void _log(boolean isOrderedByTime, StringBuilder sb) {
    final long[] total = {0};
    String newline = System.lineSeparator();

    sb.append(eventType.getSimpleName());

    if (isOrderedByTime) { sb.append(" [Ordered by time-taken]");}

    sb.append(newline);
    sb.append(line).append(newline);

    if (isOrderedByTime) {
      Map.Entry<T, Stopwatch>[] entries = events.entrySet().toArray(new Map.Entry[events.size()]);
      Arrays.sort(entries, comparator);
      for (Map.Entry<T, Stopwatch> entry : entries) {
        if (entry.getValue().elapsed(NANOSECONDS) != 0) {
          consume(total, newline, sb, entry);
        }
      }
    } else {
      for (Map.Entry<T, Stopwatch> entry : events.entrySet()) {
        if (entry.getValue().elapsed(NANOSECONDS) != 0) {
          consume(total, newline, sb, entry);
        }
      }
    }

    sb.append(line).append(newline);
    sb.append("Total time: ").append(Utils.niceTime(total[0])).append(newline);
    sb.append(line);
  }

  private static final int defaultLineLength = 30;
  private static final String line = Utils.line(defaultLineLength);
  private static final Comparator<Map.Entry<?, Stopwatch>> comparator =
      (o1, o2) -> -Long.compare(o1.getValue().elapsed(NANOSECONDS), o2.getValue().elapsed(NANOSECONDS));

  private static void consume(long[] total, String newline, StringBuilder sb, Map.Entry<?, Stopwatch> entry) {
    Stopwatch sw = entry.getValue();
    total[0] += sw.elapsed(NANOSECONDS);
    sb.append(entry.getKey()).append(" : ").append(sw.toString()).append(newline);
  }

}
