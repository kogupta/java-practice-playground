package org.kogu.practice.misc;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumUtil {
  /**
   * Calls method that directly called this utility using first the current enum element (<code>enumElem</code>).
   * If it does not implement this method directly tries to invoke this method from its parent, then from parent of parent etc.
   * If given <code>enumElem == rootElem</code> returns <code>rootValue</code>.
   * As a fallback calls method declared in enum level itself using <code>rootElem</code> passed here as argument.
   * Invokes method <code>parentAccessor</code> using reflection.
   *
   * @param <R>            the return type
   * @param enumElem
   * @param rootElem
   * @param rootValue
   * @param parentAccessor
   * @return result of the target method invocation.
   * @throws IllegalStateException that wraps real exception if something is going wrong with reflection.
   */
  public static <R> R callHierarchicalMethod(Enum<?> enumElem, Enum<?> rootElem, R rootValue, String parentAccessor) {
    if (enumElem == rootElem) {
      return rootValue;
    }
    String methodName = new Throwable().getStackTrace()[1].getMethodName();
    for (Enum<?> elem = enumElem; elem != null; elem = getParent(elem, parentAccessor)) {
      if (!elem.getClass().equals(OsType.class)) {
        try {
          return invoke(elem, elem.getClass(), methodName);
          // SecurityException and NoSuchMethodException
          // could be thrown from clazz.getMethod(). It is legal here:
          // some enum elements do not override implementation that done in parent.
          // If these exceptions are thrown we just try to find "better" method in parent enum element.
        } catch (SecurityException e) {
          continue;
        } catch (NoSuchMethodException e) {
          continue;
          // All other exceptions are thrown during method invocation itself.
          // If it happens something is going very wrong, so we just wrap these exception
          // by IllegalStateException and throw again.
        } catch (IllegalArgumentException e) {
          throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
          throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
          throw new IllegalStateException(e);
        }
      }
    }

    try {
      return invoke(rootElem, rootElem.getDeclaringClass(), methodName);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Retrieves parent enum element of given element. Just a simple wrapper over the reflection API.
   *
   * @param <E>
   * @param e
   * @param methodName name of method that retrieves parent.
   * @return the parent element
   */
  @SuppressWarnings("unchecked") // reflection API does not support generics
  private static <E extends Enum<?>> E getParent(E e, String methodName) {
    try {
      return (E) e.getClass().getMethod(methodName).invoke(e);
    } catch (Exception ex) {
      throw new IllegalArgumentException(
          "Cannot get parent enum element from enum " + e.getClass().getName() +
              " using accessor " + methodName, ex);
    }
  }

  /**
   * Invokes method on given enum element. Just a simple wrapper over the reflection API.
   *
   * @param <R>        return type
   * @param enumElem
   * @param clazz
   * @param methodName
   * @return result of method invocation
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @SuppressWarnings("unchecked") // reflection API does not support generics
  private static <R> R invoke(Enum<?> enumElem, Class<?> clazz, String methodName) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Method m = clazz.getDeclaredMethod(methodName);
    // the method must be public but the class is probably not.
    // for example it could be anonymous inner class created to implement overridden method
    m.setAccessible(true);
    return (R) m.invoke(enumElem);
  }
}
