package org.rapidpm.ddi.reflections;

import java.lang.reflect.Type;

/**
 * Created by svenruppert on 11.08.15.
 */
public class ReflectionUtils {

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
}
