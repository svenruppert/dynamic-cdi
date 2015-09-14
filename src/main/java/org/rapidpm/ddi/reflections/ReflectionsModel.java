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
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by svenruppert on 14.07.15.
 */
public class ReflectionsModel {


  //TODO refactoring to pessimistic write / concurrent read

  private boolean parallelExecutors = false;
  private Map<String, LocalDateTime> activatedPackagesMap = new ConcurrentHashMap<>();

  private Reflections reflections = new Reflections(
      createConfigurationBuilder()
          .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("org.rapidpm")))
          .setScanners(createScanners())
  );

  private Object obj = new Object();

  public ReflectionsModel() {
  }


  public void setParallelExecutors(final boolean parallelExecutors) {
    this.parallelExecutors = parallelExecutors;
  }

  public void rescann() {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .setScanners(createScanners())));
    }
  }

  private ConfigurationBuilder createConfigurationBuilder() {
    final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.setUrls(ClasspathHelper.forJavaClassPath());
    if (parallelExecutors) return configurationBuilder.useParallelExecutor();
    else return configurationBuilder;
  }

  private Scanner[] createScanners() {
    Scanner[] sccannerArray = new Scanner[3];
    sccannerArray[0] = new SubTypesScanner();
    sccannerArray[1] = new TypeAnnotationsScanner();
    sccannerArray[2] = new MethodAnnotationsScanner();
    return sccannerArray;
  }

  public void rescann(URL... urls) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .setUrls(urls)
          .setScanners(createScanners())));
      activatedPackagesMap.put("", LocalDateTime.now());
    }
  }

  public void rescann(Collection<URL> urls) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .setUrls(urls)
          .setScanners(createScanners())));
      activatedPackagesMap.put("", LocalDateTime.now());
    }
  }

  public void rescann(String pkgPrefix) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
          .setScanners(createScanners())));
      activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
    }
  }

  public void rescann(String pkgPrefix, URL... urls) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
          .setUrls(urls)
          .setScanners(createScanners())));
      activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
    }
  }

  public void rescann(String pkgPrefix, Collection<URL> urls) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
          .setUrls(urls)
          .setScanners(createScanners())));
      activatedPackagesMap.put(pkgPrefix, LocalDateTime.now());
    }
  }


  public boolean isPkgPrefixActivated(String pkgPrefix) {
    return activatedPackagesMap.containsKey(pkgPrefix);
  }

  public LocalDateTime getPkgPrefixActivatedTimestamp(String pkgPrefix) {
    return activatedPackagesMap.getOrDefault(pkgPrefix, LocalDateTime.MIN);
  }


  //delegated methods
//must be synchronized

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

