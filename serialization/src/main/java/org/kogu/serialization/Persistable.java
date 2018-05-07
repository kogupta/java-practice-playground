package org.kogu.serialization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

interface Persistable extends Externalizable {
  public abstract byte[] toByteArray();
  public abstract void readFromByteArray(byte[] bytes);

  @Override
  default void writeExternal(ObjectOutput out) throws IOException {
    System.out.println(" ...... > ");
    byte[] bytes = toByteArray();
    out.writeInt(bytes.length);
    out.write(bytes);
  }

  @Override
  default void readExternal(ObjectInput in) throws IOException {
    System.out.println(" < ......");
    int len = in.readInt();
    byte[] bytes = new byte[len];
    in.readFully(bytes);
    readFromByteArray(bytes);
  }
}
