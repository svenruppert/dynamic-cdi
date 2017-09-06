/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.rapidpm.ddi.reflections;

import org.rapidpm.frp.model.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import repacked.com.google.common.base.Predicate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.util.Collections.unmodifiableSet;


public class ReflectionsModel {


  //TODO refactoring to pessimistic write / concurrent read

  private final Map<String, LocalDateTime> activatedPackagesMap = new ConcurrentHashMap<>();
  private final Object obj = new Object();
  private final Map<Pair<String, String>, Set<Method>> methodsAnnotatedWithCache = new ConcurrentHashMap<>();
  private final Map<String, Set> subTypeOfCache = new ConcurrentHashMap<>();
  private final Map<String, Set> subTypeOfCacheWithoutInterfacesnadGenerated = new ConcurrentHashMap<>();
  private final Map<Class<? extends Annotation>, Set> typesAnnotatedWithCache = new ConcurrentHashMap<>();
  private boolean parallelExecutors;
  private final Reflections reflections = new Reflections(
      createConfigurationBuilder()
          .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("org.rapidpm")))
          .setScanners(createScanners())
  );

  public ReflectionsModel() {
  }

  public void setParallelExecutors(final boolean parallelExecutors) {
    this.parallelExecutors = parallelExecutors;
  }

  public void rescann(final String pkgPrefix) {
    rescannImpl(createConfigurationBuilder()
        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
        .setScanners(createScanners()));
    activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
  }

  private void rescannImpl(final ConfigurationBuilder configuration) {
    synchronized (obj) {
      final LocalDateTime now = LocalDateTime.now();
      final Reflections reflections = new Reflections(configuration);
      this.reflections.merge(reflections);
      refreshActivatedPkgMap(now, reflections);
      clearCaches();
    }
  }

  private ConfigurationBuilder createConfigurationBuilder() {
    final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.setUrls(ClasspathHelper.forJavaClassPath());
    if (parallelExecutors) return configurationBuilder.useParallelExecutor();
    else return configurationBuilder;
  }

  private Scanner[] createScanners() {
    final Scanner[] sccannerArray = new Scanner[4];
    sccannerArray[0] = new SubTypesScanner();
    sccannerArray[1] = new TypeAnnotationsScanner();
    sccannerArray[2] = new MethodAnnotationsScanner();
    sccannerArray[3] = new PkgTypesScanner();
//    sccannerArray[4] = new StaticMetricsProxyScanner();
//    sccannerArray[5] = new StaticLoggingProxyScanner();
    return sccannerArray;
  }

  private void refreshActivatedPkgMap(final LocalDateTime now, final Reflections reflections) {
    reflections
        .getStore()
        .get(index(PkgTypesScanner.class))
        .keySet()
        .forEach(pkgName -> activatedPackagesMap.put(pkgName, now));
  }

  public void clearCaches() {
    methodsAnnotatedWithCache.clear();
    subTypeOfCache.clear();
    subTypeOfCacheWithoutInterfacesnadGenerated.clear();
    typesAnnotatedWithCache.clear();
  }

  private String index(Class clazz) {
    return clazz.getSimpleName();
  }

  public void rescann(String pkgPrefix, URL... urls) {
    rescannImpl(createConfigurationBuilder()
        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
        .setUrls(urls)
        .setScanners(createScanners()));
    activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
  }

  public void rescann(String pkgPrefix, Collection<URL> urls) {
    rescannImpl(createConfigurationBuilder()
        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
        .setUrls(urls)
        .setScanners(createScanners()));
    activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
  }

  public Set<String> getActivatedPkgs() {
    return new HashSet<>(activatedPackagesMap.keySet());
  }

  public boolean isPkgPrefixActivated(final String pkgPrefix) {
    return activatedPackagesMap.containsKey(pkgPrefix);
  }

  public LocalDateTime getPkgPrefixActivatedTimestamp(final String pkgPrefix) {
    return activatedPackagesMap.getOrDefault(pkgPrefix, LocalDateTime.MIN);
  }

  public Collection<String> getClassesForPkg(final String pkgName) {
    final Collection<String> clsNames = reflections
        .getStore()
        .get(index(PkgTypesScanner.class))
        .get(pkgName);
    return Collections.unmodifiableCollection(clsNames);
  }

//  //TODO to complex for performance
//  public <T> Set<Class<? extends T>> getStaticMetricProxiesFor(final Class<T> type) {
//
//    final ClassLoader[] classLoaders = reflections.getConfiguration().getClassLoaders();
//
//    final Collection<String> metricProxyClassNames = reflections
//        .getStore()
//        .get(index(StaticMetricsProxyScanner.class))
//        .get(type.getName());
//
//    final List<Class<? extends T>> classes = ReflectionUtils.forNames(metricProxyClassNames, classLoaders);
//    return unmodifiableSet(new HashSet<>(classes));
//
//  }
//
//  public <T> Set<Class<? extends T>> getStaticLoggingProxiesFor(final Class<T> type) {
//    final ClassLoader[] classLoaders = reflections.getConfiguration().getClassLoaders();
//    final Collection<String> loggingProxyClassNames = reflections.getStore()
//        .get(index(StaticLoggingProxyScanner.class))
//        .get(type.getName());
//
//    final List<Class<? extends T>> classes = ReflectionUtils.forNames(loggingProxyClassNames, classLoaders);
//    return unmodifiableSet(new HashSet<>(classes));
//  }

  //delegated methods

  private final Function<Set, Set> newSet = (Function<Set, Set>) input -> {
    final HashSet<Set<Class<?>>> hashSet = new HashSet<>();
    hashSet.addAll(input);
    return hashSet;
  };

  public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
    if (subTypeOfCache.containsKey(type.getName())) {
      return (Set<Class<? extends T>>) subTypeOfCache.get(type.getName());
    }
    final Set<Class<? extends T>> subTypesOf = reflections.getSubTypesOf(type);
//    final Set<Class<? extends T>> unmodifiableSet = Collections.unmodifiableSet(subTypesOf);
    subTypeOfCache.put(type.getName(), subTypesOf);
    return subTypesOf;
//    return reflections.getSubTypesOf(type);
  }


  public <T> Set<Class<? extends T>> getSubTypesWithoutInterfacesAndGeneratedOf(final Class<T> type) {


    if (subTypeOfCacheWithoutInterfacesnadGenerated.containsKey(type.getName())) {
      return (Set<Class<? extends T>>) subTypeOfCacheWithoutInterfacesnadGenerated.get(type.getName());
    }
    final Set<Class<? extends T>> subTypesOf = reflections.getSubTypesOf(type);
    final Set<Class<? extends T>> unmodifiableSet = new ReflectionUtils().removeInterfacesAndGeneratedFromSubTypes(subTypesOf);
    subTypeOfCacheWithoutInterfacesnadGenerated.put(type.getName(), unmodifiableSet);
    return unmodifiableSet;
//    return reflections.getSubTypesOf(type);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
    if (typesAnnotatedWithCache.containsKey(annotation)) return (Set<Class<?>>) typesAnnotatedWithCache.get(annotation);

    final Set<Class<?>> typesAnnotatedWith = unmodifiableSet(reflections.getTypesAnnotatedWith(annotation));
    typesAnnotatedWithCache.put(annotation, typesAnnotatedWith);
    return typesAnnotatedWith;
//    return reflections.getTypesAnnotatedWith(annotation);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation, final boolean honorInherited) {
    return reflections.getTypesAnnotatedWith(annotation, honorInherited);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation) {
    return reflections.getTypesAnnotatedWith(annotation);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation, final boolean honorInherited) {
    return reflections.getTypesAnnotatedWith(annotation, honorInherited);
  }


  public Set<Method> getMethodsAnnotatedWith(Class clazz, final Annotation annotation) {

    final Pair<String, String> key = new Pair<>(clazz.getName(), annotation.annotationType().getName());

    if (methodsAnnotatedWithCache.containsKey(key)) return methodsAnnotatedWithCache.get(key);

    final Set<Method> allMethods = ReflectionUtils.getAllMethods(clazz,
        (Predicate<Method>) input -> input != null && input.isAnnotationPresent(annotation.annotationType()));

    final Set<Method> unmodifiableSet = unmodifiableSet(allMethods);
    methodsAnnotatedWithCache.put(key, unmodifiableSet);
    return unmodifiableSet;
  }

}

