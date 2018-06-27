/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rapidpm.ddi;


import static org.rapidpm.ddi.scopes.InjectionScopeManager.listAllActiveScopeNames;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.rapidpm.ddi.bootstrap.ClassResolverCheck001;
import org.rapidpm.ddi.implresolver.ImplementingClassResolver;
import org.rapidpm.ddi.producer.InstanceCreator;
import org.rapidpm.ddi.producer.ProducerLocator;
import org.rapidpm.ddi.reflections.ReflectionsModel;
import org.rapidpm.ddi.scopes.InjectionScopeManager;
import org.rapidpm.dependencies.core.logger.Logger;
import org.rapidpm.dependencies.core.logger.LoggingService;

public class DI {

  private static final LoggingService LOGGER = Logger.getLogger(DI.class);
  public static final String ORG_RAPIDPM_DDI_PACKAGESFILE = "org.rapidpm.ddi.packagesfile";
  private static ReflectionsModel reflectionsModel = new ReflectionsModel();
  private static boolean bootstrapedNeeded = true;


  private DI() {
  }

  public static void checkActiveModel() {
    new ClassResolverCheck001().execute();
  }

  public static synchronized void bootstrap() {
//    reflectionsModel = new ReflectionsModel();
    ImplementingClassResolver.clearCache();
    if (bootstrapedNeeded) {
      final String packageFilePath = System.getProperty(ORG_RAPIDPM_DDI_PACKAGESFILE);
      if (packageFilePath != null && ! packageFilePath.isEmpty()) {
        bootstrapFromResource(packageFilePath);
      } else {
        reflectionsModel.rescann("");
      }
    }
    bootstrapedNeeded = false;
  }

  private static void bootstrapFromResource(String path) {
    try (InputStream is = ClassLoader.getSystemResourceAsStream(path)) {
      loadJarResource(is);
    } catch (IOException e) {
      loadFilesystemResource(path , e);
    }
  }

  private static void loadFilesystemResource(String path , IOException e) {
    try (InputStream is = new FileInputStream(path)) {
      bootstrapFromResource(is);
    } catch (IOException e1) {
      LOGGER.warning(String.format("Error loading file <%s> <%s>" , path , e.getMessage()));
      throw new DDIModelException("Unable to load packages from file" , e1);
    }
  }

  private static void loadJarResource(InputStream is) throws IOException {
    if (is != null) {
      bootstrapFromResource(is);
    } else {
      throw new IOException();
    }
  }

  private static void bootstrapFromResource(InputStream inputStream) {
    String line;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      while ((line = reader.readLine()) != null) {
        reflectionsModel.rescann(line);
      }
    } catch (IOException e) {
      LOGGER.warning(String.format("Error loading packages"));
      throw new DDIModelException("Unable to load packages from file" , e);
    }
  }

  public static synchronized void clearReflectionModel() {
    reflectionsModel = new ReflectionsModel();
    clearCaches();
    InjectionScopeManager.reInitAllScopes();
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

  public static synchronized void activatePackages(String pkg , URL... urls) {
    reflectionsModel.rescann(pkg , urls);
    clearCaches();
    bootstrapedNeeded = false;
  }

  public static synchronized void activatePackages(String pkg , Collection<URL> urls) {
    reflectionsModel.rescann(pkg , urls);
    clearCaches();
    bootstrapedNeeded = false;
  }

//  @Deprecated
//  public static synchronized void activatePackages(boolean parallelExecutors , String pkg) {
//    reflectionsModel.setParallelExecutors(false);
//    reflectionsModel.rescann(pkg);
//    clearCaches();
//    bootstrapedNeeded = false;
//  }
//
//  @Deprecated
//  public static synchronized void activatePackages(boolean parallelExecutors , String pkg , URL... urls) {
//    reflectionsModel.setParallelExecutors(false);
//    reflectionsModel.rescann(pkg , urls);
//    clearCaches();
//    bootstrapedNeeded = false;
//  }
//
//  @Deprecated
//  public static synchronized void activatePackages(boolean parallelExecutors , String pkg , Collection<URL> urls) {
//    reflectionsModel.setParallelExecutors(false);
//    reflectionsModel.rescann(pkg , urls);
//    clearCaches();
//    bootstrapedNeeded = false;
//  }

  public static synchronized <T> T activateDI(T instance) {
    if (bootstrapedNeeded) bootstrap();

    injectAttributes(instance);
    initialize(instance);
    return instance;
  }

  public static synchronized <T> T activateDI(Class<T> clazz2Instanciate) {
    if (bootstrapedNeeded) bootstrap();

    final T instance = new InstanceCreator().instantiate(clazz2Instanciate);
    injectAttributes(instance);
    initialize(instance);
    return instance;
  }

  public static Set<String> listAllActiveScopes() {
    return listAllActiveScopeNames();
  }

  public static void registerClassForScope(Class clazz , String scope) {
    InjectionScopeManager.registerClassForScope(clazz , scope);
  }

  public static void deRegisterClassForScope(Class clazz) {
    InjectionScopeManager.deRegisterClassForScope(clazz);
  }


  private static <T> void injectAttributes(final T rootInstance) throws SecurityException {
    injectAttributesForClass(rootInstance.getClass() , rootInstance);
  }


  private static <T> void injectAttributesForClass(Class targetClass , T rootInstance) {
    Class<?> superclass = targetClass.getSuperclass();
    if (superclass != null) {
      injectAttributesForClass(superclass , rootInstance);
    }

    final Field[] fields = targetClass.getDeclaredFields();
    for (final Field field : fields) {
      if (field.isAnnotationPresent(Inject.class)) {

        final Class targetType = field.getType();
        Object value = new InstanceCreator().instantiate(targetType);
        DI.activateDI(value);

        if (value != null) {
          injectIntoField(field , rootInstance , value);
        }
      }
    }
  }

  private static void injectIntoField(final Field field , final Object instance , final Object target) {
    AccessController.doPrivileged((PrivilegedAction) () -> {
      boolean wasAccessible = field.isAccessible();
      field.setAccessible(true);
      try {
        field.set(instance , target);
        return null; // return nothing...
      } catch (IllegalArgumentException | IllegalAccessException ex) {
        LOGGER.warning("Cannot set field: " , ex);
        throw new IllegalStateException("Cannot set field: " + field , ex);
      } finally {
        field.setAccessible(wasAccessible);
      }
    });
  }

  private static void initialize(Object instance) {
    Class<?> clazz = instance.getClass();
    invokeMethodWithAnnotation(clazz , instance , PostConstruct.class);
  }

  private static void invokeMethodWithAnnotation(Class clazz , final Object instance ,
                                                 final Class<? extends Annotation> annotationClass)
      throws IllegalStateException, SecurityException {

    final Set<Method> methodsAnnotatedWith = reflectionsModel.getMethodsAnnotatedWith(clazz , new PostConstruct() {
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
        LOGGER.warning("method could not invoked " , e);
      }
    });
  }

  //delegator

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

  public static Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation , final boolean honorInherited) {
    return reflectionsModel.getTypesAnnotatedWith(annotation , honorInherited);
  }

  public static Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation) {
    return reflectionsModel.getTypesAnnotatedWith(annotation);
  }

  public static Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation , final boolean honorInherited) {
    return reflectionsModel.getTypesAnnotatedWith(annotation , honorInherited);
  }
}
