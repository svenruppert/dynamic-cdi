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
import org.rapidpm.ddi.bootstrap.ClassResolverCheck001;
import org.rapidpm.ddi.implresolver.DDIModelException;
import org.rapidpm.ddi.implresolver.ImplementingClassResolver;
import org.rapidpm.ddi.producer.Producer;
import org.rapidpm.proxybuilder.VirtualProxyBuilder;
import org.rapidpm.proxybuilder.type.virtual.Concurrency;
import org.rapidpm.proxybuilder.type.virtual.ProxyGenerator;
import org.rapidpm.proxybuilder.type.virtual.ProxyType;
import org.rapidpm.proxybuilder.type.virtual.dynamic.ServiceStrategyFactoryNotThreadSafe;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Produces;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Set;


/**
 * Created by Sven Ruppert on 05.12.2014.
 */
public class DI {

  private static final DI INSTANCE = new DI();

  public static DI getInstance() {
    return INSTANCE;
  }

  public static void bootstrap() {
    //hole alle Felder die mit einem @Inject versehen sind.
    //pruefe ob es sich um ein Interface handelt
    //pruefe ob es nur einen Producer / eine Implementierung  dazu gibt
    // -- liste Multiplizitäten
    new ClassResolverCheck001().execute();
  }

  private DI() {
  }


//  public static synchronized void addNewClassLoaderAndMerge(ClassLoader classLoader) {
//    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
//        .setUrls(ClasspathHelper.forPackage(""))
//        .setScanners(
//            new SubTypesScanner(),
//            new TypeAnnotationsScanner(),
//            new MethodAnnotationsScanner())
//        .addClassLoader(classLoader);
//    final Reflections reflectionToMerge = new Reflections(configurationBuilder);
//    ReflectionsSingleton.REFLECTIONS.merge(reflectionToMerge);
//  }


  public synchronized <T> void activateDI(T instance) {
    injectAttributes(instance);
    initialize(instance);
    //register at new Scope ?
  }

  private <T> void injectAttributes(final T rootInstance) throws SecurityException {
    injectAttributesForClass(rootInstance.getClass(), rootInstance);
  }


  private <T> void injectAttributesForClass(Class targetClass, T rootInstance) {
    Class<?> superclass = targetClass.getSuperclass();
    if (superclass != null) {
      injectAttributesForClass(superclass, rootInstance);
    }

    Field[] fields = targetClass.getDeclaredFields();
    for (final Field field : fields) {
      if (field.isAnnotationPresent(Inject.class)) {
        Class type = field.getType();
        final Class realClass = new ImplementingClassResolver().resolve(type);

        Object value; //Attribute Type for inject
        if (field.isAnnotationPresent(Proxy.class)) {
          final Proxy annotation = field.getAnnotation(Proxy.class);

          final boolean virtual = annotation.virtual();
          final boolean concurrent = annotation.concurrent();
          final boolean metrics = annotation.metrics();
          final boolean secure = annotation.secure(); //woher die Sec Rules?
          final boolean logging = annotation.logging();

          if (virtual) {
            //interface , realclass
            value = ProxyGenerator.newBuilder()
                .withSubject(type).withRealClass(realClass)
                .withType(ProxyType.DYNAMIC)
                .withConcurrency(Concurrency.NONE)
                .withServiceFactory(new DDIServiceFactory<>(realClass))
                .withServiceStrategyFactory(new ServiceStrategyFactoryNotThreadSafe<>())
                .build()
                .make();
          } else {
            value = instantiate(realClass);
            activateDI(value); //rekursiver abstieg
          }
          if (concurrent || metrics || secure || logging) {
            final VirtualProxyBuilder virtualProxyBuilder = VirtualProxyBuilder.createBuilder(type, value);
            if (metrics) {
              virtualProxyBuilder.addMetrics();
            }
            if (concurrent) {
              //virtualProxyBuilder.
            }
            if (secure) {
//              virtualProxyBuilder.addSecurityRule(()->{});
            }
            if (logging) {
              //virtualProxyBuilder.addLogging();
            }
            value = virtualProxyBuilder.build();
          }
        } else {
          value = instantiate(realClass);
          activateDI(value); //rekursiver abstieg
        }
        //check Scope ....
//        Object value = scopes.getProperty(clazz, key);
//        if (!type.isPrimitive()) {
//          value = instantiate(type);
//        }

        if (value != null) {
          injectIntoField(field, rootInstance, value);
        }
      }
    }
  }


  private <T> T instantiate(Class<T> clazz) {
    //check scope -> Singleton
    //check scope -> ???

    T newInstance;
    if (clazz.isInterface()) {
      final Class<T> resolve = new ImplementingClassResolver().resolve(clazz);
      newInstance = createNewInstance(clazz, resolve);
    } else {
      newInstance = createNewInstance(clazz, clazz);
    }
    return newInstance;
  }

  @Nullable
  private <T> T createNewInstance(final Class interf, final Class clazz) {
    //Producer vorhanden?

    //kann ein Interface sein, oder eine Klasse von einem ClassResolver
    final Set<Class<?>> typesAnnotatedWith = ReflectionsSingleton.REFLECTIONS.getTypesAnnotatedWith(Produces.class);

    final Iterator<Class<?>> iterator = typesAnnotatedWith.iterator();
    while (iterator.hasNext()) {
      Class producerClass = iterator.next();
      final Produces annotation = (Produces) producerClass.getAnnotation(Produces.class);
      final Class value = annotation.value();
      if (value == null) throw new DDIModelException("Producer without target Interface " + producerClass);
      if (value.equals(interf)) {
        //TODO logger
      } else {
        iterator.remove();
      }
    }

//    if (interf.isInterface() && clazz.isInterface()) throw new DDIModelException("no producer found for the interface " + clazz);

    if (typesAnnotatedWith.isEmpty()) {

      if (clazz.isInterface()) {
        throw new DDIModelException(" only interfaces found for " + interf);
      } else {
        final T newInstance;
        try {
          newInstance = (T) clazz.newInstance();
          return newInstance;
        } catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
          throw new DDIModelException(e);
        }
      }
    } else if (typesAnnotatedWith.size() > 1) {
      throw new DDIModelException(" to many producer methods found for " + interf + " - " + typesAnnotatedWith);
    } else {


      final Class cls = (Class) typesAnnotatedWith.toArray()[0];
      try {
        Producer<T> newInstance = (Producer<T>) cls.newInstance();
        activateDI(newInstance);
        return newInstance.create();
      } catch (InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
        throw new DDIModelException(e);
      }
    }
  }

  private static void injectIntoField(final Field field, final Object instance, final Object target) {
    AccessController.doPrivileged((PrivilegedAction) () -> {
      boolean wasAccessible = field.isAccessible();
      field.setAccessible(true);
      try {
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
    Class<?> clazz = instance.getClass();
    invokeMethodWithAnnotation(clazz, instance, PostConstruct.class);
  }

  private boolean isNotPrimitive(Class<?> type) {
    return !type.isPrimitive();
  }


  private static void invokeMethodWithAnnotation(Class clazz, final Object instance,
                                                 final Class<? extends Annotation> annotationClass)
      throws IllegalStateException, SecurityException {

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
