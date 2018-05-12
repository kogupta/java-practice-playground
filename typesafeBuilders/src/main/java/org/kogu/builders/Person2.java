package org.kogu.builders;

import java.util.Objects;
import java.util.function.Function;

/**
 * This is an extremely unsatisfactory solution.
 *
 * Reference: http://www.radicaljava.com/2016/09/19/safe-builder.html
 */
public class Person2 {
  public final String firstName;
  public final String lastName;
  public final int age;
  public final boolean vip;

  private Person2(String firstName, String lastName, int age, boolean vip) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.age = age;
    this.vip = vip;
  }

  public Person2(Function<Builder2<N, N>, Builder2<Y, Y>> fn) {
    this(fn.apply(new Builder2<>()));
  }

  private Person2(Builder2<Y, Y> builder2) {
    this(builder2.firstName, builder2.lastName, builder2.age, builder2.vip);
  }

  interface IsSpecified {}
  interface Y extends IsSpecified {}
  interface N extends IsSpecified {}


  public static final class Builder2<FirstName extends IsSpecified, LastName extends IsSpecified> {
    private String firstName; // required
    private String lastName;  // required
    private int age = 21;
    private boolean vip = false;

    public Builder2<Y, LastName> setFirstName(String firstName) {
      this.firstName = firstName;
      return (Builder2<Y, LastName>) this;
    }

    public Builder2<FirstName, Y> setLastName(String lastName) {
      this.lastName = lastName;
      return (Builder2<FirstName, Y>) this;
    }

    public Builder2<FirstName, LastName> setAge(int age) {
      this.age = age;
      return this;
    }

    public Builder2<FirstName, LastName> vip() {
      this.vip = true;
      return this;
    }
  }

  public static void main(String[] args) {
    Builder2 builder2 = new Builder2().setFirstName("K").setLastName("Gupta").setAge(100);
    Person2 person2 = new Person2(builder2);
    assert Objects.equals(person2.firstName, "K");
    assert Objects.equals(person2.lastName, "Gupta");
    assert Objects.equals(person2.age, 100);
    assert !person2.vip;
  }

}
