package org.kogu.practice.misc;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public enum OsType {
  OS(null),
  Windows(OS),
  WindowsNT(Windows),
  WindowsNTWorkstation(WindowsNT),
  WindowsNTServer(WindowsNT),
  Windows2000(Windows),
  Windows2000Server(Windows2000),
  Windows2000Workstation(Windows2000),
  WindowsXp(Windows),
  WindowsVista(Windows),
  Windows7(Windows),
  Windows95(Windows),
  Windows98(Windows),
  Unix(OS) {
    @Override
    public boolean supportsXWindowSystem() {
      return true;
    }
  },
  Linux(Unix),
  AIX(Unix),
  HpUx(Unix),
  SunOs(Unix),
  ;

  private OsType parent = null;
  private List<OsType> children = new ArrayList<OsType>();


  private OsType(OsType parent) {
    this.parent = parent;
    if (this.parent != null) {
      this.parent.addChild(this);
    }
  }

  public OsType parent() {
    return parent;
  }

  public boolean is(OsType other) {
    if (other == null) {
      return false;
    }

    for (OsType osType = this;  osType != null;  osType = osType.parent()) {
      if (other == osType) {
        return true;
      }
    }
    return false;
  }

  public OsType[] children() {
    return children.toArray(new OsType[children.size()]);
  }

  public OsType[] allChildren() {
    List<OsType> list = new ArrayList<OsType>();
    addChildren(this, list);
    return list.toArray(new OsType[list.size()]);
  }

  private static void addChildren(OsType root, List<OsType> list) {
    list.addAll(root.children);
    for (OsType child : root.children) {
      addChildren(child, list);
    }
  }

  private void addChild(OsType child) {
    this.children.add(child);
  }


  public boolean supportsXWindowSystem() {
    return EnumUtil.<Boolean>callHierarchicalMethod(this, OsType.OS, false, "parent");
  }

  public boolean supportsXWindowSystem1() {
    if (this == OsType.OS) {
      return false;
    }

    for (OsType t = this;  t != null;  t = t.parent()) {
      if(!t.getClass().equals(OsType.class)) {
        try {
          return (Boolean)t.getClass().
              getDeclaredMethod("supportsXWindowSystem1").
              invoke(t);
        } catch (SecurityException | NoSuchMethodException e) {
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
          throw new IllegalStateException(e);
        }
      }
    }
    return OsType.OS.supportsXWindowSystem();
  }
}
