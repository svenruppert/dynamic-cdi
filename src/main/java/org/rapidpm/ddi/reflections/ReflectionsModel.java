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

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionsModel {


  //TODO refactoring to pessimistic write / concurrent read

  private final Map<String, LocalDateTime> activatedPackagesMap = new ConcurrentHashMap<>();
  private final Object obj = new Object();
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
    }
  }

  private ConfigurationBuilder createConfigurationBuilder() {
    final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.setUrls(ClasspathHelper.forJavaClassPath());
    if (parallelExecutors) return configurationBuilder.useParallelExecutor();
    else return configurationBuilder;
  }

  private Scanner[] createScanners() {
    final Scanner[] sccannerArray = new Scanner[5];
    sccannerArray[0] = new SubTypesScanner();
    sccannerArray[1] = new TypeAnnotationsScanner();
    sccannerArray[2] = new MethodAnnotationsScanner();
    sccannerArray[3] = new PkgTypesScanner();
    sccannerArray[4] = new StaticMetricsProxyScanner();
    return sccannerArray;
  }

  private void refreshActivatedPkgMap(final LocalDateTime now, final Reflections reflections) {
    reflections
        .getStore()
        .get(index(PkgTypesScanner.class))
        .keySet()
        .forEach(pkgName -> activatedPackagesMap.put(pkgName, now));
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

  //TODO to complex for performance
  public <T> Set<Class<? extends T>> getStaticMetricProxiesFor(final Class<T> type) {

    final ClassLoader[] classLoaders = reflections.getConfiguration().getClassLoaders();

    final Collection<String> metricProxyClassNames = reflections.getStore()
        .get(index(StaticMetricsProxyScanner.class))
        .get(type.getName());

    final List<Class<? extends T>> classes = ReflectionUtils.forNames(metricProxyClassNames, classLoaders);
    return Collections.unmodifiableSet(new HashSet<>(classes));

  }

  //delegated methods

  public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
    return reflections.getSubTypesOf(type);
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
    return reflections.getTypesAnnotatedWith(annotation);
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
}

