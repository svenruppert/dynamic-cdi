package org.rapidpm.ddi.reflections;

import org.rapidpm.ddi.DDIModelException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by Sven Ruppert on 11.08.15.
 */
public class ReflectionUtils extends org.reflections.ReflectionUtils {

  public boolean checkInterface(final Type aClass, Class targetInterface) {
    if (aClass.equals(targetInterface)) return true;

    final Type[] genericInterfaces = ((Class) aClass).getGenericInterfaces();
    if (genericInterfaces.length > 0) {
      for (Type genericInterface : genericInterfaces) {
        if (genericInterface.equals(targetInterface)) return true;

        final Type[] nextLevBackArray = ((Class) genericInterface).getGenericInterfaces();
        if (nextLevBackArray.length > 0)
          for (Type type : nextLevBackArray) {
            if (checkInterface(type, targetInterface)) return true;
          }
      }
    }
    final Type genericSuperclass = ((Class) aClass).getGenericSuperclass();
    if (genericSuperclass != null) {
      if (checkInterface(genericSuperclass, targetInterface)) return true;
    }


    return false;
  }


  public <T> void setDelegatorToMetrixsProxy(T proxy, T original) {

    final String simpleName = original.getClass().getSimpleName();
    try {
      final Method declaredMethod = proxy.getClass().getDeclaredMethod("with" + simpleName, original.getClass());
      declaredMethod.invoke(proxy, original);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      throw new DDIModelException(e);
    }

  }


}
