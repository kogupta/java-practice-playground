package org.kogu.practice.http;


import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RawInputStream extends FilterInputStream implements PersistStoreStream {
  private final boolean doClose;
  private PersistStoreEntry entry;

  public RawInputStream(InputStream in, boolean close, PersistStoreEntry entry) {
    super(in);
    this.doClose = close;
    this.entry = entry;
    entry.setInputStream(this);
  }

  public void close() throws IOException {
    if (this.doClose) {
      super.close();
    }

  }

  public PersistStoreEntry getNextEntry() {
    PersistStoreEntry toRet = this.entry;
    this.entry = null;
    return toRet;
  }
}

