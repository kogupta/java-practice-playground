package org.kogu.builders;

import java.util.Objects;

/**
 * Type-safe builder with rudimentary support for optional fields.
 * Define only <b>REQUIRED</b> fields as mandated by the compiler
 * [ ordered by the lambda definitions] - then just `build`, skipping optional fields.
 *
 * Reference: https://github.com/skinny85/type-safe-builder-example
 */
public class Person3 {
  public final String firstName;  // required
  public final String lastName;   // required
  public final int age;           // optional: default value: 21
  public final boolean vip;       // optional: default value: false

  private Person3(String firstName, String lastName, int age, boolean vip) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.age = age;
    this.vip = vip;
  }

  interface FirstNameSetter {
    LastNameSetter firstName(String fName);
  }

  interface LastNameSetter {
    DefaultSetters lastName(String lName);
  }

  interface DefaultSetters {
    DefaultSetters age(int age);

    DefaultSetters isVip(boolean isVip);

    Person3 build();
  }

  public static final class Defaults {
    public static final int AGE = 21;
    public static final boolean IS_VIP = false;

    private Defaults() {}
  }

  public static final class Builder implements FirstNameSetter, LastNameSetter, DefaultSetters {
    private String firstName;
    private String lastName;
    private int age = Defaults.AGE;
    private boolean isVip = Defaults.IS_VIP;

    @Override
    public LastNameSetter firstName(String fName) {
      this.firstName = fName;
      return this;
    }

    @Override
    public DefaultSetters lastName(String lName) {
      this.lastName = lName;
      return this;
    }

    @Override
    public DefaultSetters age(int n) {
      this.age = n;
      return this;
    }

    @Override
    public DefaultSetters isVip(boolean b) {
      this.isVip = b;
      return this;
    }

    @Override
    public Person3 build() {
      return new Person3(firstName, lastName, age, isVip);
    }
  }

  public static FirstNameSetter newBuilder() {
    return new Builder();
  }

  public static void main(String[] args) {
    Person3 person3 = Person3.newBuilder()
        .firstName("K")
        .lastName("Gupta")
        .build();

    assert Objects.equals(person3.age, Defaults.AGE);
    assert Objects.equals(person3.vip, Defaults.IS_VIP);
    assert Objects.equals(person3.firstName, "K");
    assert Objects.equals(person3.lastName, "Gupta");
  }
}
