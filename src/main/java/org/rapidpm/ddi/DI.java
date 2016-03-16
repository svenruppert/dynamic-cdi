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


import org.rapidpm.ddi.Proxy.ProxyType;
import org.rapidpm.ddi.bootstrap.ClassResolverCheck001;
import org.rapidpm.ddi.implresolver.ImplementingClassResolver;
import org.rapidpm.ddi.producer.InstanceCreator;
import org.rapidpm.ddi.producer.ProducerLocator;
import org.rapidpm.ddi.reflections.ReflectionUtils;
import org.rapidpm.ddi.reflections.ReflectionsModel;
import org.rapidpm.ddi.scopes.InjectionScopeManager;
import org.rapidpm.proxybuilder.ProxyBuilder;
import org.rapidpm.proxybuilder.type.dymamic.DynamicProxyBuilder;
import org.rapidpm.proxybuilder.type.dymamic.virtual.CreationStrategy;
import org.rapidpm.proxybuilder.type.dymamic.virtual.DynamicProxyGenerator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.rapidpm.ddi.scopes.InjectionScopeManager.listAllActiveScopeNames;


public class DI {

  //  private static final ImplementingClassResolver IMPLEMENTING_CLASS_RESOLVER = new ImplementingClassResolver();
  private static final Set<String> METRICS_ACTIVATED = Collections.synchronizedSet(new HashSet<>());
  private static final Set<String> LOGGING_ACTIVATED = Collections.synchronizedSet(new HashSet<>());
  private static ReflectionsModel reflectionsModel = new ReflectionsModel();
  private static boolean bootstrapedNeeded = true;


  private DI() {
  }

  public static void checkActiveModel() {
    //hole alle Felder die mit einem @Inject versehen sind.
    //pruefe ob es sich um ein Interface handelt
    //pruefe ob es nur einen Producer / eine Implementierung  dazu gibt
    // -- liste Multiplizitäten
    new ClassResolverCheck001().execute();
  }

  public static synchronized void bootstrap() {
//    reflectionsModel = new ReflectionsModel();
    ImplementingClassResolver.clearCache();
    if (bootstrapedNeeded) {
      reflectionsModel.rescann("");
    }
    bootstrapedNeeded = false;
  }

  public static synchronized void clearReflectionModel() {
    reflectionsModel = new ReflectionsModel();
    clearCaches();
    METRICS_ACTIVATED.clear();
    LOGGING_ACTIVATED.clear();
    bootstrapedNeeded = true;
  }

  private static void clearCaches() {
    ImplementingClassResolver.clearCache();
    ProducerLocator.clearCache();
    InjectionScopeManager.cleanUp();
    reflectionsModel.clearCaches();
  }

  public static synchronized void activatePackages(Class clazz) {
    reflectionsModel.rescann(clazz.getPackage().getName());
    clearCaches();
    bootstrapedNeeded = false;
  }

  public static synchronized void activatePackages(String pkg) {
    reflectionsModel.rescann(pkg);
    clearCaches();
    bootstrapedNeeded = false;
  }

  public static synchronized void activatePackages(String pkg, URL... urls) {
    reflectionsModel.rescann(pkg, urls);
    clearCaches();
    bootstrapedNeeded = false;
  }

  public static synchronized void activatePackages(String pkg, Collection<URL> urls) {
    reflectionsModel.rescann(pkg, urls);
    clearCaches();
    bootstrapedNeeded = false;
  }

  public static synchronized void activatePackages(boolean parallelExecutors, String pkg) {
    reflectionsModel.setParallelExecutors(parallelExecutors);
    reflectionsModel.rescann(pkg);
    clearCaches();
    bootstrapedNeeded = false;
  }

  public static synchronized void activatePackages(boolean parallelExecutors, String pkg, URL... urls) {
    reflectionsModel.setParallelExecutors(parallelExecutors);
    reflectionsModel.rescann(pkg, urls);
    clearCaches();
    bootstrapedNeeded = false;
  }

  public static synchronized void activatePackages(boolean parallelExecutors, String pkg, Collection<URL> urls) {
    reflectionsModel.setParallelExecutors(parallelExecutors);
    reflectionsModel.rescann(pkg, urls);
    clearCaches();
    bootstrapedNeeded = false;
  }

  public static void activateMetrics(String pkgName) {
    final boolean pkgPrefixActivated = reflectionsModel.isPkgPrefixActivated(pkgName);
    if (pkgPrefixActivated) {
      final Collection<String> classesForPkg = reflectionsModel.getClassesForPkg(pkgName);
      METRICS_ACTIVATED.addAll(classesForPkg);
    }
  }

  public static void activateMetrics(Class clazz) {
    final boolean pkgPrefixActivated = reflectionsModel.isPkgPrefixActivated(clazz.getPackage().getName());
    if (pkgPrefixActivated) METRICS_ACTIVATED.add(clazz.getName());
  }

  public static void deActivateMetrics(String pkgName) {
    final Collection<String> classesForPkg = reflectionsModel.getClassesForPkg(pkgName);
    METRICS_ACTIVATED.removeAll(classesForPkg);
  }

  public static void deActivateMetrics(Class clazz) {
    METRICS_ACTIVATED.remove(clazz.getName());
  }

  public static synchronized <T> T activateDI(T instance) {
    if (bootstrapedNeeded) bootstrap();

//    if (InjectionScopeManager.isManagedByMe(instance.getClass())) {
//      final String scopeName = InjectionScopeManager.scopeForClass(instance.getClass());
//      System.out.println("instance.getClass() = " + instance.getClass() + " is normally managed by a Scope " + scopeName);
//    }

    injectAttributes(instance);
    initialize(instance);
    final Class<T> aClass = (Class<T>) instance.getClass();
    return createMetricsProxy(aClass, instance);
  }

  public static Set<String> listAllActiveScopes() {
    return listAllActiveScopeNames();
  }

  public static void registerClassForScope(Class clazz, String scope) {
    InjectionScopeManager.registerClassForScope(clazz, scope);
  }

  public static void deRegisterClassForScope(Class clazz) {
    InjectionScopeManager.deRegisterClassForScope(clazz);
  }

  public static synchronized <T> T activateDI(Class<T> clazz2Instanciate) {
    if (bootstrapedNeeded) bootstrap();
    final T instance = new InstanceCreator().instantiate(clazz2Instanciate);
    injectAttributes(instance);
    initialize(instance);
    return createMetricsProxy(clazz2Instanciate, instance);
  }


  private static <T> T createMetricsProxy(Class<T> clazz2Instanciate, T instance) {
    if (METRICS_ACTIVATED.contains(clazz2Instanciate.getName())) {
      //Metrics Adapter available ?
      //InMemoryCompile ?

      final Set<Class<? extends T>> staticMetricProxiesFor = getStaticMetricProxiesFor(clazz2Instanciate);
      if (staticMetricProxiesFor.isEmpty()) {
        if (clazz2Instanciate.isInterface()) {
          return ProxyBuilder.newDynamicProxyBuilder(clazz2Instanciate, instance).addMetrics().build();
        }
        //TODO try inMemoryCompile
        //ByteCode if expl. allowed ?
      } else if (staticMetricProxiesFor.size() == 1) {
        final Class<T> c = (Class<T>) staticMetricProxiesFor.toArray()[0];
        final T metricsProxy = new InstanceCreator().instantiate(c);
        new ReflectionUtils().setDelegatorToMetrixsProxy(metricsProxy, instance);
        return metricsProxy;
      } else {
        throw new DDIModelException("to many MetricProxies for " + clazz2Instanciate + " -> Proxies are " + staticMetricProxiesFor);
      }
    }
    return instance;
  }


  private static <T> void injectAttributes(final T rootInstance) throws SecurityException {
    injectAttributesForClass(rootInstance.getClass(), rootInstance);
  }


  private static <T> void injectAttributesForClass(Class targetClass, T rootInstance) {
    Class<?> superclass = targetClass.getSuperclass();
    if (superclass != null) {
      injectAttributesForClass(superclass, rootInstance);
    }

    final Field[] fields = targetClass.getDeclaredFields();
    for (final Field field : fields) {
      if (field.isAnnotationPresent(Inject.class)) {

        final Class targetType = field.getType();
        Object value; //Attribute Type for inject

        if (field.isAnnotationPresent(Proxy.class)) {
          value = createProxy(field, targetType);
        } else {
          value = new InstanceCreator().instantiate(targetType);
        }
        if (value != null) {
          injectIntoField(field, rootInstance, value);
        }
      }
    }
  }

  private static Object createProxy(final Field field, final Class targetType) {
    Object value;


    //TODO cross check with activated Metrics.. avoid double Metrics Proxy

    final Proxy annotation = field.getAnnotation(Proxy.class);

    final boolean virtual = annotation.virtual();
    final CreationStrategy creationStrategy = annotation.concurrent();
    final boolean metrics = annotation.metrics();
    final boolean secure = annotation.secure(); //woher die Sec Rules?
    final boolean logging = annotation.logging();
    final ProxyType proxyType = annotation.proxyType();
    switch (proxyType) {
      case AUTODETECT:
        break;
      case DYNAMIC:
        break;
      case GENERATED:
        break;
      case STATIC:
        break;
      default:
        break;
    }
    //just now, only dynamic version is created..
    if (virtual) {
      value = DynamicProxyGenerator.newBuilder()
          .withSubject(targetType)
          .withCreationStrategy(CreationStrategy.NO_DUPLICATES)
//                .withServiceFactory(new DDIServiceFactory<>(realClass)) //TODO Test it
          .withServiceFactory(new DDIServiceFactory<>(targetType)) //TODO Test it
          .withCreationStrategy(creationStrategy)
//                .withServiceStrategyFactory(new ServiceStrategyFactoryNotThreadSafe<>())
          .build()
          .make();
    } else {
//            value = new InstanceCreator().instantiate(realClass); // TODO Test it
      value = new InstanceCreator().instantiate(targetType); // TODO Test it
      //activateDI(value); //rekursiver abstieg
    }


    if (metrics || secure || logging) {
      final DynamicProxyBuilder dynamicProxyBuilder = DynamicProxyBuilder.createBuilder(targetType, value);
      if (metrics) {
        dynamicProxyBuilder.addMetrics();
      }
      if (secure) {
//              virtualProxyBuilder.addSecurityRule(()->{});
      }
      if (logging) {
        //virtualProxyBuilder.addLogging();
      }
      value = dynamicProxyBuilder.build();
    }
    return value;
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

  private static void initialize(Object instance) {
    Class<?> clazz = instance.getClass();
    invokeMethodWithAnnotation(clazz, instance, PostConstruct.class);
  }

  private static void invokeMethodWithAnnotation(Class clazz, final Object instance,
                                                 final Class<? extends Annotation> annotationClass)
      throws IllegalStateException, SecurityException {

    final Set<Method> methodsAnnotatedWith = reflectionsModel.getMethodsAnnotatedWith(clazz, new PostConstruct() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return PostConstruct.class;
      }
    });

    methodsAnnotatedWith.forEach(m -> {
      try {
        final boolean accessible = m.isAccessible();
        m.setAccessible(true);
        m.invoke(instance);
        m.setAccessible(accessible);
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    });


//    Method[] declaredMethods = clazz.getDeclaredMethods();
//    for (final Method method : declaredMethods) {
//      if (method.isAnnotationPresent(annotationClass)) {
//        AccessController.doPrivileged((PrivilegedAction) () -> {
//          boolean wasAccessible = method.isAccessible();
//          try {
//            method.setAccessible(true);
//            return method.invoke(instance); //TODO Dynamic ObjectAdapter ?
//          } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//            throw new IllegalStateException("Problem invoking " + annotationClass + " : " + method, ex);
//          } finally {
//            method.setAccessible(wasAccessible);
//          }
//        });
//      }
//    }
//    Class superclass = clazz.getSuperclass();
//    if (superclass != null) {
//      invokeMethodWithAnnotation(superclass, instance, annotationClass);
//    }
  }


  //delegator

  public static Set<String> listAllActivatedMetrics() {
    return Collections.unmodifiableSet(METRICS_ACTIVATED);
  }


  public static <T> Set<Class<? extends T>> getStaticMetricProxiesFor(final Class<T> type) {
    return reflectionsModel.getStaticMetricProxiesFor(type);
  }

  public static <T> Class<? extends T> resolveImplementingClass(final Class<T> interf) {
    return ImplementingClassResolver.resolve(interf);
  }

  public static boolean isPkgPrefixActivated(final String pkgPrefix) {
    return reflectionsModel.isPkgPrefixActivated(pkgPrefix);
  }

  public static boolean isPkgPrefixActivated(final Class clazz) {
    return reflectionsModel.isPkgPrefixActivated(clazz.getPackage().getName());
  }

  public static LocalDateTime getPkgPrefixActivatedTimestamp(final String pkgPrefix) {
    return reflectionsModel.getPkgPrefixActivatedTimestamp(pkgPrefix);
  }

  public static <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
    return reflectionsModel.getSubTypesOf(type);
  }

  public static <T> Set<Class<? extends T>> getSubTypesWithoutInterfacesAndGeneratedOf(final Class<T> type) {
    return reflectionsModel.getSubTypesWithoutInterfacesAndGeneratedOf(type);
  }

  public static Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
    return reflectionsModel.getTypesAnnotatedWith(annotation);
  }

  public static Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation, final boolean honorInherited) {
    return reflectionsModel.getTypesAnnotatedWith(annotation, honorInherited);
  }

  public static Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation) {
    return reflectionsModel.getTypesAnnotatedWith(annotation);
  }

  public static Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation, final boolean honorInherited) {
    return reflectionsModel.getTypesAnnotatedWith(annotation, honorInherited);
  }
}
