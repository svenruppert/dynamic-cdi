package org.rapidpm.ddi.reflections;

import org.jetbrains.annotations.NotNull;
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
import java.util.Collection;
import java.util.Set;

/**
 * Created by svenruppert on 14.07.15.
 */
public class ReflectionsModel {


  //TODO refactoring to pessimistic write / concurrent read

  private boolean parallelExecutors = false;

  private Reflections reflections = new Reflections(new ConfigurationBuilder()
      .setUrls(ClasspathHelper.forPackage("org.rapidpm"))
      .addClassLoader(ClassLoader.getSystemClassLoader())
      .addClassLoader(ReflectionsModel.class.getClassLoader())
      .setScanners(createScanners())
  );;

  private Object obj = new Object();

  public ReflectionsModel() {
  }


  public void setParallelExecutors(final boolean parallelExecutors) {
    this.parallelExecutors = parallelExecutors;
  }

  private Scanner[] createScanners(){
    Scanner[] sccannerArray = new Scanner[3];
    sccannerArray[0] = new SubTypesScanner();
    sccannerArray[1] = new TypeAnnotationsScanner();
    sccannerArray[2] = new MethodAnnotationsScanner();
    return sccannerArray;
  }

  public void rescann(ClassLoader classLoader) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .addClassLoader(classLoader)
          .setScanners(createScanners())));
    }
  }

  @NotNull
  private ConfigurationBuilder createConfigurationBuilder() {
    if (parallelExecutors)
      return new ConfigurationBuilder().useParallelExecutor();
    return new ConfigurationBuilder();
  }

  public void rescann(ClassLoader classLoader, URL... urls) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .setUrls(urls)
          .addClassLoader(classLoader)
          .setScanners(createScanners())));
    }
  }

  public void rescann(ClassLoader classLoader, Collection<URL> urls) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .setUrls(urls)
          .addClassLoader(classLoader)
          .setScanners(createScanners())));
    }
  }
  public void rescann(ClassLoader classLoader, String pkgPrefix) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
          .addClassLoader(classLoader)
          .setScanners(createScanners())));
    }
  }

  public void rescann(ClassLoader classLoader, String pkgPrefix, URL... urls ) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
          .setUrls(urls)
          .addClassLoader(classLoader)
          .setScanners(createScanners())));
    }
  }

  public void rescann(ClassLoader classLoader, String pkgPrefix, Collection<URL> urls) {
    synchronized (obj) {
      reflections.merge(new Reflections(createConfigurationBuilder()
          .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pkgPrefix)))
          .setUrls(urls)
          .addClassLoader(classLoader)
          .setScanners(createScanners())));
    }
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

