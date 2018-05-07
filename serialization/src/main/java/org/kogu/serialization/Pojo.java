package org.kogu.serialization;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "mlogent",
    "mtgtguid",
    "muploadid",
    "mtgttype",
    "mtgt",
    "msrcid",
    "time",
    "mparserid2",
    "dur",
    "mbody",
    "sefLogFormat",
    "method",
    "msecrsrctype",
    "tranprot",
    "sefActorEPProgramName",
    "norm_hashid",
    "contszin",
    "msecactorip",
    "msecactoracct",
    "usrname",
    "msecrsrcname",
    "actn",
    "msecresult",
    "mseccategory",
    "contszout",
    "conttype",
    "sefObserverEPProduct",
    "usrag",
    "desturl",
    "status",
    "group",
    "sefObserverEPManufacturer",
    "mdstime",
    "magtime",
    "mtenantid"
})
public class Pojo {
  @JsonProperty("id") private String id;
  @JsonProperty("mlogent") private String mlogent;
  @JsonProperty("mtgtguid") private String mtgtguid;
  @JsonProperty("muploadid") private long muploadid;
  @JsonProperty("mtgttype") private String mtgttype;
  @JsonProperty("mtgt") private String mtgt;
  @JsonProperty("msrcid") private long msrcid;
  @JsonProperty("time") private String time;
  @JsonProperty("mparserid2") private long mparserid2;
  @JsonProperty("dur") private double dur;
  @JsonProperty("mbody") private String mbody;
  @JsonProperty("sefLogFormat") private String sefLogFormat;
  @JsonProperty("method") private String method;
  @JsonProperty("msecrsrctype") private String msecrsrctype;
  @JsonProperty("tranprot") private String tranprot;
  @JsonProperty("sefActorEPProgramName") private String sefActorEPProgramName;
  @JsonProperty("norm_hashid") private String normHashid;
  @JsonProperty("contszin") private double contszin;
  @JsonProperty("msecactorip") private String msecactorip;
  @JsonProperty("msecactoracct") private String msecactoracct;
  @JsonProperty("usrname") private String usrname;
  @JsonProperty("msecrsrcname") private String msecrsrcname;
  @JsonProperty("actn") private String actn;
  @JsonProperty("msecresult") private String msecresult;
  @JsonProperty("mseccategory") private String mseccategory;
  @JsonProperty("contszout") private double contszout;
  @JsonProperty("conttype") private String conttype;
  @JsonProperty("sefObserverEPProduct") private String sefObserverEPProduct;
  @JsonProperty("usrag") private String usrag;
  @JsonProperty("desturl") private String desturl;
  @JsonProperty("status") private String status;
  @JsonProperty("group") private String group;
  @JsonProperty("sefObserverEPManufacturer") private String sefObserverEPManufacturer;
  @JsonProperty("mdstime") private String mdstime;
  @JsonProperty("magtime") private String magtime;
  @JsonProperty("mtenantid") private String mtenantid;
  @JsonIgnore private Map<String, Object> additionalProperties = new HashMap<>();

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  public Pojo withId(String id) {
    this.id = id;
    return this;
  }

  @JsonProperty("mlogent")
  public String getMlogent() {
    return mlogent;
  }

  @JsonProperty("mlogent")
  public void setMlogent(String mlogent) {
    this.mlogent = mlogent;
  }

  public Pojo withMlogent(String mlogent) {
    this.mlogent = mlogent;
    return this;
  }

  @JsonProperty("mtgtguid")
  public String getMtgtguid() {
    return mtgtguid;
  }

  @JsonProperty("mtgtguid")
  public void setMtgtguid(String mtgtguid) {
    this.mtgtguid = mtgtguid;
  }

  public Pojo withMtgtguid(String mtgtguid) {
    this.mtgtguid = mtgtguid;
    return this;
  }

  @JsonProperty("muploadid")
  public long getMuploadid() {
    return muploadid;
  }

  @JsonProperty("muploadid")
  public void setMuploadid(long muploadid) {
    this.muploadid = muploadid;
  }

  public Pojo withMuploadid(long muploadid) {
    this.muploadid = muploadid;
    return this;
  }

  @JsonProperty("mtgttype")
  public String getMtgttype() {
    return mtgttype;
  }

  @JsonProperty("mtgttype")
  public void setMtgttype(String mtgttype) {
    this.mtgttype = mtgttype;
  }

  public Pojo withMtgttype(String mtgttype) {
    this.mtgttype = mtgttype;
    return this;
  }

  @JsonProperty("mtgt")
  public String getMtgt() {
    return mtgt;
  }

  @JsonProperty("mtgt")
  public void setMtgt(String mtgt) {
    this.mtgt = mtgt;
  }

  public Pojo withMtgt(String mtgt) {
    this.mtgt = mtgt;
    return this;
  }

  @JsonProperty("msrcid")
  public long getMsrcid() {
    return msrcid;
  }

  @JsonProperty("msrcid")
  public void setMsrcid(long msrcid) {
    this.msrcid = msrcid;
  }

  public Pojo withMsrcid(long msrcid) {
    this.msrcid = msrcid;
    return this;
  }

  @JsonProperty("time")
  public String getTime() {
    return time;
  }

  @JsonProperty("time")
  public void setTime(String time) {
    this.time = time;
  }

  public Pojo withTime(String time) {
    this.time = time;
    return this;
  }

  @JsonProperty("mparserid2")
  public long getMparserid2() {
    return mparserid2;
  }

  @JsonProperty("mparserid2")
  public void setMparserid2(long mparserid2) {
    this.mparserid2 = mparserid2;
  }

  public Pojo withMparserid2(long mparserid2) {
    this.mparserid2 = mparserid2;
    return this;
  }

  @JsonProperty("dur")
  public double getDur() {
    return dur;
  }

  @JsonProperty("dur")
  public void setDur(double dur) {
    this.dur = dur;
  }

  public Pojo withDur(double dur) {
    this.dur = dur;
    return this;
  }

  @JsonProperty("mbody")
  public String getMbody() {
    return mbody;
  }

  @JsonProperty("mbody")
  public void setMbody(String mbody) {
    this.mbody = mbody;
  }

  public Pojo withMbody(String mbody) {
    this.mbody = mbody;
    return this;
  }

  @JsonProperty("sefLogFormat")
  public String getSefLogFormat() {
    return sefLogFormat;
  }

  @JsonProperty("sefLogFormat")
  public void setSefLogFormat(String sefLogFormat) {
    this.sefLogFormat = sefLogFormat;
  }

  public Pojo withSefLogFormat(String sefLogFormat) {
    this.sefLogFormat = sefLogFormat;
    return this;
  }

  @JsonProperty("method")
  public String getMethod() {
    return method;
  }

  @JsonProperty("method")
  public void setMethod(String method) {
    this.method = method;
  }

  public Pojo withMethod(String method) {
    this.method = method;
    return this;
  }

  @JsonProperty("msecrsrctype")
  public String getMsecrsrctype() {
    return msecrsrctype;
  }

  @JsonProperty("msecrsrctype")
  public void setMsecrsrctype(String msecrsrctype) {
    this.msecrsrctype = msecrsrctype;
  }

  public Pojo withMsecrsrctype(String msecrsrctype) {
    this.msecrsrctype = msecrsrctype;
    return this;
  }

  @JsonProperty("tranprot")
  public String getTranprot() {
    return tranprot;
  }

  @JsonProperty("tranprot")
  public void setTranprot(String tranprot) {
    this.tranprot = tranprot;
  }

  public Pojo withTranprot(String tranprot) {
    this.tranprot = tranprot;
    return this;
  }

  @JsonProperty("sefActorEPProgramName")
  public String getSefActorEPProgramName() {
    return sefActorEPProgramName;
  }

  @JsonProperty("sefActorEPProgramName")
  public void setSefActorEPProgramName(String sefActorEPProgramName) {
    this.sefActorEPProgramName = sefActorEPProgramName;
  }

  public Pojo withSefActorEPProgramName(String sefActorEPProgramName) {
    this.sefActorEPProgramName = sefActorEPProgramName;
    return this;
  }

  @JsonProperty("norm_hashid")
  public String getNormHashid() {
    return normHashid;
  }

  @JsonProperty("norm_hashid")
  public void setNormHashid(String normHashid) {
    this.normHashid = normHashid;
  }

  public Pojo withNormHashid(String normHashid) {
    this.normHashid = normHashid;
    return this;
  }

  @JsonProperty("contszin")
  public double getContszin() {
    return contszin;
  }

  @JsonProperty("contszin")
  public void setContszin(double contszin) {
    this.contszin = contszin;
  }

  public Pojo withContszin(double contszin) {
    this.contszin = contszin;
    return this;
  }

  @JsonProperty("msecactorip")
  public String getMsecactorip() {
    return msecactorip;
  }

  @JsonProperty("msecactorip")
  public void setMsecactorip(String msecactorip) {
    this.msecactorip = msecactorip;
  }

  public Pojo withMsecactorip(String msecactorip) {
    this.msecactorip = msecactorip;
    return this;
  }

  @JsonProperty("msecactoracct")
  public String getMsecactoracct() {
    return msecactoracct;
  }

  @JsonProperty("msecactoracct")
  public void setMsecactoracct(String msecactoracct) {
    this.msecactoracct = msecactoracct;
  }

  public Pojo withMsecactoracct(String msecactoracct) {
    this.msecactoracct = msecactoracct;
    return this;
  }

  @JsonProperty("usrname")
  public String getUsrname() {
    return usrname;
  }

  @JsonProperty("usrname")
  public void setUsrname(String usrname) {
    this.usrname = usrname;
  }

  public Pojo withUsrname(String usrname) {
    this.usrname = usrname;
    return this;
  }

  @JsonProperty("msecrsrcname")
  public String getMsecrsrcname() {
    return msecrsrcname;
  }

  @JsonProperty("msecrsrcname")
  public void setMsecrsrcname(String msecrsrcname) {
    this.msecrsrcname = msecrsrcname;
  }

  public Pojo withMsecrsrcname(String msecrsrcname) {
    this.msecrsrcname = msecrsrcname;
    return this;
  }

  @JsonProperty("actn")
  public String getActn() {
    return actn;
  }

  @JsonProperty("actn")
  public void setActn(String actn) {
    this.actn = actn;
  }

  public Pojo withActn(String actn) {
    this.actn = actn;
    return this;
  }

  @JsonProperty("msecresult")
  public String getMsecresult() {
    return msecresult;
  }

  @JsonProperty("msecresult")
  public void setMsecresult(String msecresult) {
    this.msecresult = msecresult;
  }

  public Pojo withMsecresult(String msecresult) {
    this.msecresult = msecresult;
    return this;
  }

  @JsonProperty("mseccategory")
  public String getMseccategory() {
    return mseccategory;
  }

  @JsonProperty("mseccategory")
  public void setMseccategory(String mseccategory) {
    this.mseccategory = mseccategory;
  }

  public Pojo withMseccategory(String mseccategory) {
    this.mseccategory = mseccategory;
    return this;
  }

  @JsonProperty("contszout")
  public double getContszout() {
    return contszout;
  }

  @JsonProperty("contszout")
  public void setContszout(double contszout) {
    this.contszout = contszout;
  }

  public Pojo withContszout(double contszout) {
    this.contszout = contszout;
    return this;
  }

  @JsonProperty("conttype")
  public String getConttype() {
    return conttype;
  }

  @JsonProperty("conttype")
  public void setConttype(String conttype) {
    this.conttype = conttype;
  }

  public Pojo withConttype(String conttype) {
    this.conttype = conttype;
    return this;
  }

  @JsonProperty("sefObserverEPProduct")
  public String getSefObserverEPProduct() {
    return sefObserverEPProduct;
  }

  @JsonProperty("sefObserverEPProduct")
  public void setSefObserverEPProduct(String sefObserverEPProduct) {
    this.sefObserverEPProduct = sefObserverEPProduct;
  }

  public Pojo withSefObserverEPProduct(String sefObserverEPProduct) {
    this.sefObserverEPProduct = sefObserverEPProduct;
    return this;
  }

  @JsonProperty("usrag")
  public String getUsrag() {
    return usrag;
  }

  @JsonProperty("usrag")
  public void setUsrag(String usrag) {
    this.usrag = usrag;
  }

  public Pojo withUsrag(String usrag) {
    this.usrag = usrag;
    return this;
  }

  @JsonProperty("desturl")
  public String getDesturl() {
    return desturl;
  }

  @JsonProperty("desturl")
  public void setDesturl(String desturl) {
    this.desturl = desturl;
  }

  public Pojo withDesturl(String desturl) {
    this.desturl = desturl;
    return this;
  }

  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(String status) {
    this.status = status;
  }

  public Pojo withStatus(String status) {
    this.status = status;
    return this;
  }

  @JsonProperty("group")
  public String getGroup() {
    return group;
  }

  @JsonProperty("group")
  public void setGroup(String group) {
    this.group = group;
  }

  public Pojo withGroup(String group) {
    this.group = group;
    return this;
  }

  @JsonProperty("sefObserverEPManufacturer")
  public String getSefObserverEPManufacturer() {
    return sefObserverEPManufacturer;
  }

  @JsonProperty("sefObserverEPManufacturer")
  public void setSefObserverEPManufacturer(String sefObserverEPManufacturer) {
    this.sefObserverEPManufacturer = sefObserverEPManufacturer;
  }

  public Pojo withSefObserverEPManufacturer(String sefObserverEPManufacturer) {
    this.sefObserverEPManufacturer = sefObserverEPManufacturer;
    return this;
  }

  @JsonProperty("mdstime")
  public String getMdstime() {
    return mdstime;
  }

  @JsonProperty("mdstime")
  public void setMdstime(String mdstime) {
    this.mdstime = mdstime;
  }

  public Pojo withMdstime(String mdstime) {
    this.mdstime = mdstime;
    return this;
  }

  @JsonProperty("magtime")
  public String getMagtime() {
    return magtime;
  }

  @JsonProperty("magtime")
  public void setMagtime(String magtime) {
    this.magtime = magtime;
  }

  public Pojo withMagtime(String magtime) {
    this.magtime = magtime;
    return this;
  }

  @JsonProperty("mtenantid")
  public String getMtenantid() {
    return mtenantid;
  }

  @JsonProperty("mtenantid")
  public void setMtenantid(String mtenantid) {
    this.mtenantid = mtenantid;
  }

  public Pojo withMtenantid(String mtenantid) {
    this.mtenantid = mtenantid;
    return this;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  public Pojo withAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
    return this;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", id).append("mlogent", mlogent).append("mtgtguid", mtgtguid).append("muploadid", muploadid).append("mtgttype", mtgttype).append("mtgt", mtgt).append("msrcid", msrcid).append("time", time).append("mparserid2", mparserid2).append("dur", dur).append("mbody", mbody).append("sefLogFormat", sefLogFormat).append("method", method).append("msecrsrctype", msecrsrctype).append("tranprot", tranprot).append("sefActorEPProgramName", sefActorEPProgramName).append("normHashid", normHashid).append("contszin", contszin).append("msecactorip", msecactorip).append("msecactoracct", msecactoracct).append("usrname", usrname).append("msecrsrcname", msecrsrcname).append("actn", actn).append("msecresult", msecresult).append("mseccategory", mseccategory).append("contszout", contszout).append("conttype", conttype).append("sefObserverEPProduct", sefObserverEPProduct).append("usrag", usrag).append("desturl", desturl).append("status", status).append("group", group).append("sefObserverEPManufacturer", sefObserverEPManufacturer).append("mdstime", mdstime).append("magtime", magtime).append("mtenantid", mtenantid).append("additionalProperties", additionalProperties).toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(dur).append(mtgttype).append(sefLogFormat).append(msecrsrctype).append(tranprot).append(sefActorEPProgramName).append(mlogent).append(contszin).append(magtime).append(msecresult).append(mseccategory).append(muploadid).append(contszout).append(mtgtguid).append(conttype).append(sefObserverEPProduct).append(id).append(usrag).append(desturl).append(group).append(sefObserverEPManufacturer).append(mbody).append(method).append(mtgt).append(msecactorip).append(msecactoracct).append(usrname).append(msecrsrcname).append(actn).append(mtenantid).append(normHashid).append(msrcid).append(time).append(mparserid2).append(additionalProperties).append(status).append(mdstime).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;

    if (!(other instanceof Pojo)) return false;

    Pojo rhs = ((Pojo) other);
    return new EqualsBuilder().append(dur, rhs.dur).append(mtgttype, rhs.mtgttype).append(sefLogFormat, rhs.sefLogFormat).append(msecrsrctype, rhs.msecrsrctype).append(tranprot, rhs.tranprot).append(sefActorEPProgramName, rhs.sefActorEPProgramName).append(mlogent, rhs.mlogent).append(contszin, rhs.contszin).append(magtime, rhs.magtime).append(msecresult, rhs.msecresult).append(mseccategory, rhs.mseccategory).append(muploadid, rhs.muploadid).append(contszout, rhs.contszout).append(mtgtguid, rhs.mtgtguid).append(conttype, rhs.conttype).append(sefObserverEPProduct, rhs.sefObserverEPProduct).append(id, rhs.id).append(usrag, rhs.usrag).append(desturl, rhs.desturl).append(group, rhs.group).append(sefObserverEPManufacturer, rhs.sefObserverEPManufacturer).append(mbody, rhs.mbody).append(method, rhs.method).append(mtgt, rhs.mtgt).append(msecactorip, rhs.msecactorip).append(msecactoracct, rhs.msecactoracct).append(usrname, rhs.usrname).append(msecrsrcname, rhs.msecrsrcname).append(actn, rhs.actn).append(mtenantid, rhs.mtenantid).append(normHashid, rhs.normHashid).append(msrcid, rhs.msrcid).append(time, rhs.time).append(mparserid2, rhs.mparserid2).append(additionalProperties, rhs.additionalProperties).append(status, rhs.status).append(mdstime, rhs.mdstime).isEquals();
  }
}
