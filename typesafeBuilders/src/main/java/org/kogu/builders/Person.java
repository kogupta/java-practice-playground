package org.kogu.builders;

import java.util.Objects;

/**
 * This is really nice type-safe way to build an object.
 * All fields <b>HAVE</b> to be defined - it may be annoying, but works.
 *
 * Reference: http://benjiweber.co.uk/blog/2014/11/02/builder-pattern-with-java-8-lambdas/
 */
public class Person {
  public final String firstName;
  public final String lastName;
  public final int age;
  public final boolean vip;

  private Person(String firstName, String lastName, int age, boolean vip) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.age = age;
    this.vip = vip;
  }

  interface FirstNameBuilder {
    LastNameBuilder firstName(String firstName);
  }

  interface LastNameBuilder {
    AgeBuilder lastName(String lastName);
  }

  interface AgeBuilder {
    VipBuilder age(int n);

    default VipBuilder defaultAge() {
      return age(21);
    }
  }

  interface VipBuilder {
    Person isVip(boolean b);

    default Person vip() {
      return isVip(false);
    }
  }

  public static FirstNameBuilder builder() {
    return fName -> lName -> age -> isVip -> new Person(fName, lName, age, isVip);
  }


  public static void main(String[] args) {
    Person person = builder().firstName("K").lastName("Gupta").age(100).isVip(false);
    check(person, 100);

    Person person2 = builder().firstName("K").lastName("Gupta").defaultAge().vip();
    check(person2, 21);
  }

  private static void check(Person person, int age) {
    assert Objects.equals(person.firstName, "K");
    assert Objects.equals(person.lastName, "Gupta");
    assert (person.age == age);
    assert !person.vip;
  }
}
