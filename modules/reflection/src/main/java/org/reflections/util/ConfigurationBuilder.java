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
package org.reflections.util;

import org.rapidpm.dependencies.core.logger.Logger;
import org.rapidpm.dependencies.core.logger.LoggingService;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.adapters.JavaReflectionAdapter;
import org.reflections.adapters.JavassistAdapter;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.serializers.Serializer;
import org.reflections.serializers.XmlSerializer;
import repacked.com.google.common.base.Predicate;
import repacked.com.google.common.collect.ObjectArrays;
import repacked.com.google.common.util.concurrent.ThreadFactoryBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class ConfigurationBuilder implements Configuration {
  /*lazy*/ protected MetadataAdapter metadataAdapter;
  @Nonnull private Set<Scanner>       scanners;
  @Nonnull private Set<URL>           urls;
  @Nullable private Predicate<String> inputsFilter;
  /*lazy*/ private Serializer serializer;
  @Nullable private ExecutorService executorService;
  @Nullable private ClassLoader[] classLoaders;
  private boolean expandSuperTypes = true;

  public ConfigurationBuilder() {
    scanners = new HashSet<>(Arrays.asList(new TypeAnnotationsScanner() , new SubTypesScanner()));
    urls = new HashSet<>();
  }


  @SuppressWarnings("unchecked")
  public static ConfigurationBuilder build(@Nullable final Object... params) {
    ConfigurationBuilder builder = new ConfigurationBuilder();

    //flatten
    List<Object> parameters = new ArrayList();
    if (params != null) {
      for (Object param : params) {
        if (param != null) {
          if (param.getClass().isArray()) {
            for (Object p : (Object[]) param) if (p != null) parameters.add(p);
          } else if (param instanceof Iterable) {
            for (Object p : (Iterable) param) if (p != null) parameters.add(p);
          } else parameters.add(param);
        }
      }
    }

    List<ClassLoader> loaders = new ArrayList();
    for (Object param : parameters) if (param instanceof ClassLoader) loaders.add((ClassLoader) param);

    ClassLoader[] classLoaders = loaders.isEmpty() ? null : loaders.toArray(new ClassLoader[loaders.size()]);
    FilterBuilder filter       = new FilterBuilder();
    List<Scanner> scanners     = new ArrayList();

    for (Object param : parameters) {
      if (param instanceof String) {
        builder.addUrls(ClasspathHelper.forPackage((String) param , classLoaders));
        filter.includePackage((String) param);
      } else if (param instanceof Class) {
        if (Scanner.class.isAssignableFrom((Class) param)) {
          try {
            builder.addScanners(((Scanner) ((Class) param).newInstance()));
          } catch (Exception e) { /*fallback*/ }
        }
        builder.addUrls(ClasspathHelper.forClass((Class) param , classLoaders));
        filter.includePackage(((Class) param));
      } else if (param instanceof Scanner) {
        scanners.add((Scanner) param);
      } else if (param instanceof URL) {
        builder.addUrls((URL) param);
      } else if (param instanceof ClassLoader) { /* already taken care */ } else if (param instanceof Predicate) {
        filter.add((Predicate<String>) param);
      } else if (param instanceof ExecutorService) {
        builder.setExecutorService((ExecutorService) param);
      } else {
        throw new ReflectionsException("could not use param " + param);
      }
    }

    if (builder.getUrls().isEmpty()) {
      if (classLoaders != null) {
        builder.addUrls(ClasspathHelper.forClassLoader(classLoaders)); //default urls getResources("")
      } else {
        builder.addUrls(ClasspathHelper.forClassLoader()); //default urls getResources("")
      }
    }

    builder.filterInputsBy(filter);
    if (! scanners.isEmpty()) {
      builder.setScanners(scanners.toArray(new Scanner[scanners.size()]));
    }
    if (! loaders.isEmpty()) {
      builder.addClassLoaders(loaders);
    }

    return builder;
  }


  public ConfigurationBuilder addUrls(final Collection<URL> urls) {
    this.urls.addAll(urls);
    return this;
  }


  public ConfigurationBuilder addScanners(final Scanner... scanners) {
    this.scanners.addAll(Arrays.asList(scanners));
    return this;
  }


  public ConfigurationBuilder addUrls(final URL... urls) {
    this.urls.addAll(Arrays.asList(urls));
    return this;
  }


  public ConfigurationBuilder filterInputsBy(Predicate<String> inputsFilter) {
    this.inputsFilter = inputsFilter;
    return this;
  }


  public ConfigurationBuilder addClassLoaders(Collection<ClassLoader> classLoaders) {
    return addClassLoaders(classLoaders.toArray(new ClassLoader[classLoaders.size()]));
  }


  public ConfigurationBuilder addClassLoaders(ClassLoader... classLoaders) {
    this.classLoaders = this.classLoaders == null ? classLoaders : ObjectArrays.concat(this.classLoaders , classLoaders , ClassLoader.class);
    return this;
  }

  public ConfigurationBuilder forPackages(String... packages) {
    for (String pkg : packages) {
      addUrls(ClasspathHelper.forPackage(pkg));
    }
    return this;
  }

  @Nonnull
  public Set<Scanner> getScanners() {
    return scanners;
  }


  public ConfigurationBuilder setScanners(@Nonnull final Scanner... scanners) {
    this.scanners.clear();
    return addScanners(scanners);
  }

  @Nonnull
  public Set<URL> getUrls() {
    return urls;
  }

  public ConfigurationBuilder setUrls(final URL... urls) {
    this.urls = new HashSet<>(Arrays.asList(urls));
    return this;
  }

  public MetadataAdapter getMetadataAdapter() {
    if (metadataAdapter != null) return metadataAdapter;
    else {
      try {
        return (metadataAdapter = new JavassistAdapter());
      } catch (Throwable e) {
        final LoggingService log = Logger.getLogger(Reflections.class);
        if (log != null)
          log.warning("could not create JavassistAdapter, using JavaReflectionAdapter" , e);
        return (metadataAdapter = new JavaReflectionAdapter());
      }
    }
  }

  public ConfigurationBuilder setMetadataAdapter(final MetadataAdapter metadataAdapter) {
    this.metadataAdapter = metadataAdapter;
    return this;
  }

  @Nullable
  public Predicate<String> getInputsFilter() {
    return inputsFilter;
  }

  public void setInputsFilter(@Nullable Predicate<String> inputsFilter) {
    this.inputsFilter = inputsFilter;
  }

  @Nullable
  public ExecutorService getExecutorService() {
    return executorService;
  }

  public ConfigurationBuilder setExecutorService(@Nullable ExecutorService executorService) {
    this.executorService = executorService;
    return this;
  }

  public Serializer getSerializer() {
    return serializer != null ? serializer : (serializer = new XmlSerializer()); //lazily defaults to XmlSerializer
  }

  public ConfigurationBuilder setSerializer(Serializer serializer) {
    this.serializer = serializer;
    return this;
  }

  @Nullable
  public ClassLoader[] getClassLoaders() {
    return classLoaders;
  }

  public void setClassLoaders(@Nullable ClassLoader[] classLoaders) {
    this.classLoaders = classLoaders;
  }

  @Override
  public boolean shouldExpandSuperTypes() {
    return expandSuperTypes;
  }

  public ConfigurationBuilder setUrls(@Nonnull final Collection<URL> urls) {
    this.urls = new HashSet<>(urls);
    return this;
  }

  public ConfigurationBuilder useParallelExecutor() {
    return useParallelExecutor(Runtime.getRuntime().availableProcessors());
  }


  public ConfigurationBuilder useParallelExecutor(final int availableProcessors) {
    ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("org.reflections-scanner-%d").build();
    setExecutorService(Executors.newFixedThreadPool(availableProcessors , factory));
    return this;
  }


  public ConfigurationBuilder setExpandSuperTypes(boolean expandSuperTypes) {
    this.expandSuperTypes = expandSuperTypes;
    return this;
  }


  public ConfigurationBuilder addClassLoader(ClassLoader classLoader) {
    return addClassLoaders(classLoader);
  }
}
