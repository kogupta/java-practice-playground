package org.kogu.practice.functions;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ToString<T> {
  public default String autoToString() {
    return "{" +
        props().stream()
            .map(prop -> (Object)prop.apply((T)this))
            .map(prop -> prop == null ? "_" : prop.toString())
            .collect(Collectors.joining(", ")) +
        "}";
  }

  List<Function<T,?>> props();
}
