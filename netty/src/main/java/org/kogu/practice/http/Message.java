package org.kogu.practice.http;


import java.util.*;

public class Message {
  private final List<Message.ObjectInfo> objects;
  private final List<Message.ContentInfo> contents;
  private final String dateTime;
  private final String updatedTime;
  private final String sdkVersion;
  private final String MAJOR;
  private final String MINOR;
  private final String REVISION;

  public Message() {
    this((List) Collections.emptyList(), (List)Collections.emptyList(), (String)null);
  }

  public Message(List<Message.ObjectInfo> objects, List<Message.ContentInfo> contents, String dateTime) {
    this(objects, contents, dateTime, dateTime);
  }

  public Message(List<Message.ObjectInfo> objects, List<Message.ContentInfo> contents, String dateTime, String updatedTime) {
    this.objects = new ArrayList();
    this.contents = new ArrayList();
    this.MAJOR = "1";
    this.MINOR = "1";
    this.REVISION = "3";
    this.objects.addAll(objects);
    this.contents.addAll(contents);
    this.dateTime = dateTime;
    this.updatedTime = updatedTime;
    this.sdkVersion = "1.1.3";
  }

  public Message(Message.ObjectInfo object, Message.ContentInfo content, String dateTime) {
    this(object, content, dateTime, dateTime);
  }

  public Message(Message.ObjectInfo object, Message.ContentInfo content, String dateTime, String updatedTime) {
    this.objects = new ArrayList();
    this.contents = new ArrayList();
    this.MAJOR = "1";
    this.MINOR = "1";
    this.REVISION = "3";
    if (object != null) {
      this.objects.add(object);
    }

    if (content != null) {
      this.contents.add(content);
    }

    this.dateTime = dateTime;
    this.updatedTime = updatedTime;
    this.sdkVersion = "1.1.3";
  }

  public boolean equals(Object o) {
    if (this == o) { return true;}

    if (!(o instanceof Message)) { return false;}

    Message other = (Message) o;
    return dateTime.equals(other.dateTime) &&
        updatedTime.equals(other.updatedTime) &&
        objects.equals(other.objects) &&
        contents.equals(other.contents);
  }

  public List<Message.ContentInfo> getContents() {
    return this.contents;
  }

  public String getDateTime() {
    return this.dateTime;
  }

  public List<Message.ObjectInfo> getObjects() {
    return this.objects;
  }

  public String getSdkVersion() {
    return this.sdkVersion;
  }

  public String getUpdatedTime() {
    return this.updatedTime;
  }

  public static class ObjectInfo {
    private final String fileName;
    private final Message.MergeMethod mergeMethod;
    private final String objectPath;
    private final String contentLength;
    private final Map<String, AttachedData> attachedData;

    public ObjectInfo() {
      this((String)null, (String)null, Collections.emptyMap(), Message.MergeMethod.NONE);
    }

    public ObjectInfo(String objectPath, String fileName, Map<String, Message.AttachedData> attachedData, Message.MergeMethod mergeMethod) {
      this(objectPath, fileName, attachedData, mergeMethod, (String)null);
    }

    public ObjectInfo(String objectPath, String fileName, Map<String, Message.AttachedData> attachedData, Message.MergeMethod mergeMethod, String contentLength) {
      this.attachedData = new HashMap();
      this.objectPath = objectPath;
      this.fileName = fileName;
      this.mergeMethod = mergeMethod;
      this.attachedData.putAll(attachedData);
      this.contentLength = contentLength;
    }

    public boolean equals(Object o) {
      if (this == o) {
        return true;
      } else if (!(o instanceof Message.ObjectInfo)) {
        return false;
      } else if (!this.mergeMethod.equals(((Message.ObjectInfo)o).mergeMethod)) {
        return false;
      } else if (!this.objectPath.equals(((Message.ObjectInfo)o).objectPath)) {
        return false;
      } else {
        return this.attachedData.equals(((Message.ObjectInfo)o).attachedData);
      }
    }

    public Map<String, Message.AttachedData> getAttachedData() {
      return this.attachedData;
    }

    public String getContentLength() {
      return this.contentLength;
    }

    public String getFileName() {
      return this.fileName;
    }

    public Message.MergeMethod getMergeMethod() {
      return this.mergeMethod;
    }

    public String getObjectPath() {
      return this.objectPath;
    }
  }

  public enum MergeMethod {
    ZIP,
    NONE_EXTERNAL,
    NONE;

    private MergeMethod() {
    }
  }

  public static class ContentInfo {
    private final byte[] content;
    private final Message.AttachedData attachedData;

    public ContentInfo() {
      this((byte[])null, (Message.AttachedData)null);
    }

    public ContentInfo(byte[] content, Message.AttachedData attachedData) {
      this.content = content;
      this.attachedData = attachedData;
    }

    public boolean equals(Object o) {
      if (this == o) {
        return true;
      } else if (!(o instanceof Message.ContentInfo)) {
        return false;
      } else if (!this.content.equals(((Message.ContentInfo)o).content)) {
        return false;
      } else {
        return this.attachedData.equals(((Message.ContentInfo)o).attachedData);
      }
    }

    public Message.AttachedData getAttachedData() {
      return this.attachedData;
    }

    public byte[] getContent() {
      return this.content;
    }
  }

  public static class AttachedData {
    private final String requestGuid;
    private final String requestTime;
    private final String metadata;
    private final String tenantId;
    private final String contentType;
    private final String xRemoteUser;
    private final String oamRemoteUser;
    private final String principalUserName;

    public AttachedData() {
      this((String)null, (String)null, (String)null, (String)null, (String)null);
    }

    public AttachedData(String metadata, String tenantId, String contentType) {
      this((String)null, (String)null, metadata, tenantId, contentType);
    }

    public AttachedData(String reqGuid, String reqTime, String metadata, String tenantId, String contentType) {
      this(reqGuid, reqTime, metadata, tenantId, contentType, (String)null, (String)null, (String)null);
    }

    public AttachedData(String reqGuid, String reqTime, String metadata, String tenantId, String contentType, String xRemoteUser, String oamRemoteUser, String principalUserName) {
      this.requestGuid = reqGuid;
      this.requestTime = reqTime;
      this.metadata = metadata;
      this.tenantId = tenantId;
      this.contentType = contentType;
      this.xRemoteUser = xRemoteUser;
      this.oamRemoteUser = oamRemoteUser;
      this.principalUserName = principalUserName;
    }

    public boolean equals(Object o) {
      if (this == o) {
        return true;
      } else if (!(o instanceof Message.AttachedData)) {
        return false;
      } else if (!this.requestGuid.equals(((Message.AttachedData)o).requestGuid)) {
        return false;
      } else if (!this.requestTime.equals(((Message.AttachedData)o).requestTime)) {
        return false;
      } else if (!this.tenantId.equals(((Message.AttachedData)o).tenantId)) {
        return false;
      } else if (!this.contentType.equals(((Message.AttachedData)o).contentType)) {
        return false;
      } else {
        return this.metadata.equals(((Message.AttachedData)o).metadata);
      }
    }

    public String getContentType() {
      return this.contentType;
    }

    public String getMetadata() {
      return this.metadata;
    }

    public String getOamRemoteUser() {
      return this.oamRemoteUser;
    }

    public String getPrincipalUserName() {
      return this.principalUserName;
    }

    public String getRequestGuid() {
      return this.requestGuid;
    }

    public String getRequestTime() {
      return this.requestTime;
    }

    public String getTenantId() {
      return this.tenantId;
    }

    public String getXRemoteUser() {
      return this.xRemoteUser;
    }
  }
}

