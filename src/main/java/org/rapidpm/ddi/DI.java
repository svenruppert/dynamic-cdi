/*
 * Copyright [2014] [www.rapidpm.org / Sven Ruppert (sven.ruppert@rapidpm.org)]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.rapidpm.ddi;


import org.jetbrains.annotations.Nullable;
import org.rapidpm.proxybuilder.ProxyBuilder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;


/**
 * Created by Sven Ruppert on 05.12.2014.
 */
public class DI {


  public static void bootstrap() {
    //hole alle Felder die mit einem @Inject versehen sind.
    //pruefe ob es sich um ein Interface handelt
    //pruefe ob es nur einen Producer / eine Implementierung  dazu gibt
    // -- liste Multiplizitäten




  }


  public <T> void activateDI(T instance) {
    injectAttributes(instance);
    initialize(instance);
    //register at new Scope ?
  }

  private <T> void injectAttributes(final T instance) throws SecurityException {
    final Class<?> clazz = instance.getClass();
    injectAttributesForClass(clazz, instance);
    Class<? extends Object> superclass = clazz.getSuperclass();
    if (superclass != null) {
      injectAttributesForClass(superclass, instance);
    }
  }


  private <T> void injectAttributesForClass(Class<?> clazz, T instance) {
    Field[] fields = clazz.getDeclaredFields();
    for (final Field field : fields) {
      if (field.isAnnotationPresent(Inject.class)) {
        Class<?> type = field.getType();

        T value = null;
        if (field.isAnnotationPresent(Proxy.class)){
          final Proxy annotation = field.getAnnotation(Proxy.class);
          final boolean virtual = annotation.virtual();

//          if (virtual){
//            value = createProxy(interfaceClazz, impleClazz, cdiBuilder);
//          }

//          ProxyBuilder.createBuilder(clazz, ).
        } else {


        }

        //check Scope ....
//        Object value = scopes.getProperty(clazz, key);
        if (!type.isPrimitive()) {
          value = instantiate(type);
        }
        if (value != null) {
          activateDI(value); //rekursiver abstieg
        }
        if (value != null) {
          injectIntoField(field, instance, value);
        }
      }
    }
  }


  public <T> T instantiate(Class clazz) {
    //check scope -> Singleton
    //check scope -> ???

    T newInstance = null;
    if (clazz.isInterface()) {
      final Set subTypesOf = ReflectionsSingleton.REFLECTIONS.getSubTypesOf(clazz);
      if (subTypesOf.isEmpty()) {
        throw new DDIModelException("could not find an implementation for " + clazz);
      } else if(subTypesOf.size() == 1){
        newInstance = createNewInstance((Class) subTypesOf.toArray()[0]);
      } else {
        throw new DDIModelException("interface with multiple implementations= " + clazz);
      }
    } else {
      newInstance = createNewInstance(clazz);
    }


    return newInstance;
  }

  @Nullable
  private <T> T createNewInstance(final Class clazz) {
    System.out.println("newInstance for clazz = " + clazz);
    final T newInstance;
    try {
      newInstance = (T) clazz.newInstance();
      return newInstance;
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static void injectIntoField(final Field field, final Object instance, final Object target) {
    AccessController.doPrivileged((PrivilegedAction) () -> {
      boolean wasAccessible = field.isAccessible();
      try {
        field.setAccessible(true);
        field.set(instance, target);
        return null; // return nothing...
      } catch (IllegalArgumentException | IllegalAccessException ex) {
        throw new IllegalStateException("Cannot set field: " + field, ex);
      } finally {
        field.setAccessible(wasAccessible);
      }
    });
  }

  private void initialize(Object instance) {
    Class<? extends Object> clazz = instance.getClass();
    invokeMethodWithAnnotation(clazz, instance, PostConstruct.class
    );
  }

  private boolean isNotPrimitive(Class<?> type) {
    return !type.isPrimitive();
  }


  private static void invokeMethodWithAnnotation(Class clazz, final Object instance,
                                                 final Class<? extends Annotation> annotationClass) throws IllegalStateException, SecurityException {
    Method[] declaredMethods = clazz.getDeclaredMethods();
    for (final Method method : declaredMethods) {
      if (method.isAnnotationPresent(annotationClass)) {
        AccessController.doPrivileged((PrivilegedAction) () -> {
          boolean wasAccessible = method.isAccessible();
          try {
            method.setAccessible(true);
            return method.invoke(instance, new Object[]{}); //TODO Dynamic ObjectAdapter ?
          } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IllegalStateException("Problem invoking " + annotationClass + " : " + method, ex);
          } finally {
            method.setAccessible(wasAccessible);
          }
        });
      }
    }
    Class superclass = clazz.getSuperclass();
    if (superclass != null) {
      invokeMethodWithAnnotation(superclass, instance, annotationClass);
    }
  }
}
