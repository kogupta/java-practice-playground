package org.kogu.practice.struct;

public class Test {
  interface StructField {
    byte index();
    byte size();
  }

  interface IntField extends StructField {
    @Override default byte size() { return 4;}
  }

  interface LongField extends StructField {
    @Override default byte size() { return 8;}
  }

}
