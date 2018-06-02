package org.kogu.practice.http;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PersistStoreInputStream extends InputStream implements PersistStoreStream {
  private final ZipInputStream zipIs;
  private final boolean doClose;
  private final boolean filterTenants;
  private Properties toc;
  private PersistStoreStream currentIs;
  private ZipEntry zEntry;
  private boolean readTOC;
  private final Message.ObjectInfo objectInfo;
  private Properties tenantIDMapping;
  private final String rootEntryName;
  private final String rootTenantID;
  private String zdtIdentifier;
  private long zdtTime;

  private static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

  private static Properties getTenantIDMapFromEntry(ZipEntry entry) throws IOException {
    if (entry == null) {
      return null;
    } else {
      Properties mapping = null;
      byte[] tenantIDMapBytes = entry.getExtra();
      if (tenantIDMapBytes != null) {
        ByteArrayInputStream bais = new ByteArrayInputStream(tenantIDMapBytes);
        mapping = new Properties();
        mapping.load(bais);
      }

      return mapping;
    }
  }

  /**
   * @deprecated
   */
  @Deprecated
  public PersistStoreInputStream(InputStream in) {
    this(in, null);
  }

  private PersistStoreInputStream(InputStream in, Message.ObjectInfo objectInfo) {
    this(in, true, objectInfo, null, null, null, true, null, 0L);
  }

  private PersistStoreInputStream(InputStream in,
                                  boolean close,
                                  Message.ObjectInfo objectInfo,
                                  String rootEntryName,
                                  Properties tenantIDMapping,
                                  String rootTenantID,
                                  boolean filterTenants,
                                  String requestIdentifier,
                                  long requestTime) {
    this.zipIs = new ZipInputStream(in);
    this.doClose = close;
    this.filterTenants = filterTenants;
    this.readTOC = false;
    this.zEntry = null;
    this.currentIs = null;
    this.tenantIDMapping = tenantIDMapping;
    this.objectInfo = objectInfo;
    this.rootEntryName = rootEntryName;
    this.rootTenantID = rootTenantID;

    this.zdtIdentifier = requestIdentifier;
    this.zdtTime = requestTime;
  }

  public void close() throws IOException {
    if (this.doClose) {
      this.zipIs.close();
    }

  }

  public PersistStoreEntry getNextEntry() throws IOException {
    boolean extraRead = false;
    if (!this.readTOC) {
      this.zEntry = this.readTOC();
      if (this.zEntry != null) {
        extraRead = true;
      }

      this.readTOC = true;
    }

    PersistStoreEntry pEntry = null;

    do {
      if (this.currentIs == null) {
        if (!extraRead) {
          this.zEntry = this.zipIs.getNextEntry();
        }

        if (this.zEntry != null) {
          String currentEntryName = this.zEntry.getName();
          if (currentEntryName != null && currentEntryName.startsWith("Entry_") && currentEntryName.endsWith("]")) {
            this.zdtIdentifier = currentEntryName.substring(currentEntryName.indexOf("[") + 1, currentEntryName.lastIndexOf("]"));
            int extraLen = this.zEntry.getExtra() != null ? this.zEntry.getExtra().length : 0;
            if (extraLen > 0) {
              this.zdtTime = Long.parseLong(new String(this.zEntry.getExtra()));
            }
          }

          String currentTenantID = this.lookupTenantID(currentEntryName);
          boolean supportedTenant = true;
          if (this.filterTenants) {
            supportedTenant = this.isSupportedTenant();
          }

          if (this.doClose && supportedTenant) {
            this.currentIs = this.getFormattedInputStream(this.zEntry, currentEntryName, currentTenantID, this.zdtIdentifier, this.zdtTime);
          } else {
            if (currentTenantID != null && this.doClose) {
              continue;
            }

            this.currentIs = this.getFormattedInputStream(this.zEntry, this.rootEntryName, this.rootTenantID, this.zdtIdentifier, this.zdtTime);
          }
        }
      } else {
        pEntry = this.currentIs.getNextEntry();
        if (pEntry == null) {
          this.currentIs = null;
        }
      }
    } while (pEntry == null && this.zEntry != null);

    return pEntry;
  }

  public int read() throws IOException {
    return this.zipIs.read();
  }

  public int read(byte[] b, int off, int len) throws IOException {
    return this.zipIs.read(b, off, len);
  }

  private Message.AttachedData getAttachedData(String entryName) {
    if (this.objectInfo == null) {
      return null;
    } else {
      Message.AttachedData attachedData = null;
      Map<String, Message.AttachedData> attachedDataMap = null;
      attachedDataMap = this.objectInfo.getAttachedData();
      if (attachedDataMap == null) {
        throw new IllegalStateException("attachedData Map cannot be null");
      } else {
        if (this.isMerged()) {
          if (isEmpty(entryName)) {
            throw new IllegalArgumentException("entryname to lookup cannot be null or empty");
          }

          attachedData = attachedDataMap.get(entryName);
        } else {
          Set<Entry<String, Message.AttachedData>> entrySet = attachedDataMap.entrySet();
          if (entrySet.size() != 1) {
            throw new IllegalStateException("PersistStoreInputStream: Cannot resolve object id to extract attached data");
          }

          Entry<String, Message.AttachedData> entry = entrySet.iterator().next();

          attachedData = entry.getValue();
        }

        return attachedData;
      }
    }
  }

  private PersistStoreStream getFormattedInputStream(ZipEntry entry, String topEntryName, String topTenantID, String rootEntryIdentifer, long rootEntryTime) {
    PersistStoreStream is = null;
    if (entry == null) {
      throw new IllegalArgumentException("entry cannot be null");
    } else {
      String entryName = entry.getName();
      String computedRootEntryName = topEntryName != null ? topEntryName : entryName;
      String computedTenantID = topTenantID != null ? topTenantID : this.lookupTenantID(computedRootEntryName);

      Message.AttachedData attachedData = null;
      if (!isEmpty(entryName) && this.toc != null && !isEmpty(this.toc.getProperty(entryName))) {
        String format = this.toc.getProperty(entryName);
        PersistStoreEntry.PersistStoreFormat storeFormat = PersistStoreEntry.PersistStoreFormat.fromString(format);
        if (storeFormat != PersistStoreEntry.PersistStoreFormat.ZIP_OF_ENTRIES && storeFormat != PersistStoreEntry.PersistStoreFormat.ZIP_OF_ZIPS) {
          attachedData = this.getAttachedData(computedRootEntryName);
          PersistStoreEntry targetEntry = new PersistStoreEntry(entry, attachedData, computedTenantID, rootEntryIdentifer, rootEntryTime);
          is = new RawInputStream(this.zipIs, false, targetEntry);
        } else {
          is = new PersistStoreInputStream(this.zipIs, false, this.objectInfo, computedRootEntryName, this.tenantIDMapping, computedTenantID, this.filterTenants, rootEntryIdentifer, rootEntryTime);
        }

        return is;
      } else {
        attachedData = this.getAttachedData(computedRootEntryName);
        PersistStoreEntry targetEntry = new PersistStoreEntry(entry, attachedData, computedTenantID, rootEntryIdentifer, rootEntryTime);
        return new RawInputStream(this.zipIs, false, targetEntry);
      }
    }
  }

  private boolean isMerged() {
    return this.objectInfo != null && this.objectInfo.getMergeMethod() != null && this.objectInfo.getMergeMethod() == Message.MergeMethod.ZIP;
  }

  private boolean isSupportedTenant() { return true;}

  private String lookupTenantID(String entryName) { return entryName;}

  private ZipEntry readTOC() throws IOException {
    ZipEntry entry;
    entry = this.zipIs.getNextEntry();
    if (entry != null && entry.getName().equals("TOC")) {
      this.toc = new Properties();
      this.toc.load(this.zipIs);
      if (this.tenantIDMapping == null) {
        this.tenantIDMapping = getTenantIDMapFromEntry(entry);
      }

      entry = null;
    }

    return entry;
  }
}
