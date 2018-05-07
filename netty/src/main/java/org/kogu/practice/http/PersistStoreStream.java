package org.kogu.practice.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

public interface PersistStoreStream {
  PersistStoreStream.PersistStoreEntry getNextEntry() throws IOException;

  public static class PersistStoreEntry {
    public static final String PERSIST_STORE_TOC_ENTRY_NAME = "TOC";
    public static final String PERSIST_STORE_MD_TENANTID = "PSMD_TENANTID";
    public static final String PERSIST_STORE_MD_ENTITYID = "PSMD_ENTITYID";
    private final ZipEntry entry;
    private InputStream in;
    private final Message.AttachedData attachedData;
    private final String tenantID;
    private final String zdtIdentifier;
    private final long zdtTime;

    /** @deprecated */
    @Deprecated
    public PersistStoreEntry(ZipEntry entry) {
      this(entry, (Message.AttachedData)null);
    }

    public PersistStoreEntry(ZipEntry entry, Message.AttachedData attachedData) {
      this(entry, attachedData, (String)null, (String)null, 0L);
    }

    protected PersistStoreEntry(ZipEntry entry, Message.AttachedData attachedData, String tenantID, String identifier, long time) {
      this.entry = entry;
      this.in = null;
      this.attachedData = attachedData;
      this.tenantID = tenantID;
      this.zdtIdentifier = identifier;
      this.zdtTime = time;
    }

    public Message.AttachedData getAttachedData() {
      Message.AttachedData ret = this.attachedData;
      if (ret == null) {
        ret = this.buildAttachedData(this.tenantID);
      }

      return ret;
    }

    public long getCrc() {
      return this.entry.getCrc();
    }

    public byte[] getExtra() {
      return this.entry.getExtra();
    }

    public InputStream getInputStream() {
      return this.in;
    }

    public String getName() {
      return this.entry.getName();
    }

    public String getRequestIdentifier() {
      return this.zdtIdentifier;
    }

    public long getRequestTime() {
      return this.zdtTime;
    }

    public long getSize() {
      return this.entry.getSize();
    }

    public long getTime() {
      return this.entry.getTime();
    }

    public void setInputStream(InputStream in) {
      this.in = in;
    }

    private Message.AttachedData buildAttachedData(String tenantID) {
      Message.AttachedData attData = null;
      if (tenantID != null) {
        attData = new Message.AttachedData("", "", "", tenantID, "");
      }

      return attData;
    }

    public static enum PersistStoreFormat {
      ZIP_OF_ENTRIES("ZIPOFENTRIES"),
      ZIP_OF_ZIPS("ZIPOFZIPS"),
      RAW("RAW");

      private String name;

      public static PersistStoreStream.PersistStoreEntry.PersistStoreFormat fromString(String format) {
        PersistStoreStream.PersistStoreEntry.PersistStoreFormat psFormat = RAW;
        if (format != null && !format.isEmpty()) {
          PersistStoreStream.PersistStoreEntry.PersistStoreFormat[] arr$ = values();
          int len$ = arr$.length;

          for(int i$ = 0; i$ < len$; ++i$) {
            PersistStoreStream.PersistStoreEntry.PersistStoreFormat f = arr$[i$];
            if (f.getFormat().equals(format)) {
              psFormat = f;
              break;
            }
          }

          return psFormat;
        } else {
          return psFormat;
        }
      }

      private PersistStoreFormat(String name) {
        this.name = name;
      }

      public String getFormat() {
        return this.name;
      }
    }
  }
}
