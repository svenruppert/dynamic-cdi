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

package org.rapidpm.module.iot.cdi;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
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


  public <T> void activateCDI(T instance){
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
        final String key = field.isAnnotationPresent(Named.class) ? field.getAnnotation(Named.class).value() : field.getName();
        System.out.println("key = " + key);
        //check Scope ....
//        Object value = scopes.getProperty(clazz, key);
        T value = null;
        if (value == null && !type.isPrimitive()) {
          value = instantiate(type);
        }
        if(value != null){
          activateCDI(value); //rekursiver abstieg
        }
        if (value != null) {
          injectIntoField(field, instance, value);
        }
      }
    }
  }


  public <T> T instantiate(Class clazz) {
  //check scope -> Singleton
  //check scope -> Prototype
  //check scope -> ???

//    T newInstance = null;
//    if(clazz.isInterface()){
//      //  hole alle implementierungen
//      //transient
//      Reflections reflections = new Reflections(new ConfigurationBuilder()
//          .setUrls(asList(ClasspathHelper.forClass(clazz)))
////          .filterInputsBy(TestModelFilter)
//          .setScanners(
//              new SubTypesScanner(false),
//              new TypeAnnotationsScanner(),
//              new FieldAnnotationsScanner(),
//              new MethodAnnotationsScanner(),
//              new MethodParameterScanner(),
//              new MethodParameterNamesScanner(),
//              new MemberUsageScanner()));
//      final Set<Class<T>> subTypesOf = reflections.getSubTypesOf(clazz);
//      //Multiplizitaeten   -> Proxy gegen NPE ?
//      System.out.println("subTypesOf = " + subTypesOf);
//
//      for (final Class<T> t : subTypesOf) {
//        try {
//          newInstance =  t.newInstance();
//          return newInstance;
//        } catch (InstantiationException | IllegalAccessException e) {
//          e.printStackTrace();
//        }
//      }
//
//    } else{
//      try {
//        newInstance = (T) clazz.newInstance();
//        return newInstance;
//      } catch (InstantiationException | IllegalAccessException e) {
//        e.printStackTrace();
//      }
//    }
//    return null;
//
//    Object product = modelsAndServices.get(clazz);
//    if (product == null) {
//      Object instance = instanceSupplier.instantiate(clazz);
//      product = instanceSupplier.isInjectionAware() ? instance : injectAndInitialize(instance);
//      if (!instanceSupplier.isScopeAware()) {
//        modelsAndServices.putIfAbsent(clazz, product);
//      }
//    }
//    return product;

    //TODO first Test Implementation - must be removed
    try {
      return (T) clazz.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

//  private InstanceProvider getDefaultInstanceSupplier() {
//    return (c) -> {
//      if (c.isInterface()) {
//        // For an interface default behavior is to delegate to standard JRE loading mechanism ServiceLoader
//        Iterator itProviders = ServiceLoader.load(c).iterator();
//        if (itProviders.hasNext()) {
//          return itProviders.next();
//        }
//        throw new IllegalStateException("Cannot, via ServiceLoader, instanciate an object from interface: " + c);
//      } else {
//        try {
//          // It's a class, let's try to instantiate it directly
//          return c.newInstance();
//        } catch (InstantiationException | IllegalAccessException ex) {
//          throw new IllegalStateException("Cannot instantiate: " + c, ex);
//        }
//      }
//    };
//  }


//  private <T> Supplier<T> createOrGetSupplier(){
//    return () -> (T) new Object();
//  }



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

//  private static boolean isNotPrimitiveOrString(Class<?> type) {
//    return !type.isPrimitive() && !type.isAssignableFrom(String.class);
//  }
  private boolean isNotPrimitive(Class<?> type) {
    return !type.isPrimitive();
  }



  static void invokeMethodWithAnnotation(Class clazz, final Object instance,
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
