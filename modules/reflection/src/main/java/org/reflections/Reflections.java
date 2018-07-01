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
package org.reflections;

import org.rapidpm.dependencies.core.logger.Logger;
import org.rapidpm.dependencies.core.logger.LoggingService;
import org.reflections.scanners.*;
import org.reflections.scanners.Scanner;
import org.reflections.serializers.Serializer;
import org.reflections.serializers.XmlSerializer;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.reflections.util.Utils;
import org.reflections.vfs.Vfs;
import repacked.com.google.common.base.Joiner;
import repacked.com.google.common.base.Predicate;
import repacked.com.google.common.collect.Iterables;
import repacked.com.google.common.collect.Multimap;
import repacked.com.google.common.collect.MultimapImpl;
import repacked.com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static org.reflections.ReflectionUtils.*;
import static org.reflections.util.Utils.*;
import static repacked.com.google.common.base.Predicates.in;
import static repacked.com.google.common.base.Predicates.not;
import static repacked.com.google.common.collect.Iterables.concat;

public class Reflections {
  @Nullable
  private static final LoggingService LOGGER = Logger.getLogger(Reflections.class);

  protected final transient Configuration configuration;
  protected Store store;


  public Reflections(final Configuration configuration) {
    this.configuration = configuration;
    store = new Store(configuration);

    if (configuration.getScanners() != null && ! configuration.getScanners().isEmpty()) {
      //inject to scanners
      for (Scanner scanner : configuration.getScanners()) {
        scanner.setConfiguration(configuration);
        scanner.setStore(store.getOrCreate(scanner.getClass().getSimpleName()));
      }

      scan();

      if (configuration.shouldExpandSuperTypes()) {
        expandSuperTypes();
      }
    }
  }

  public Reflections(final String prefix , @Nullable final Scanner... scanners) {
    this((Object) prefix , scanners);
  }


  public Reflections(final Object... params) {
    this(ConfigurationBuilder.build(params));
  }

  protected Reflections() {
    configuration = new ConfigurationBuilder();
    store = new Store(configuration);
  }


  public static Reflections collect() {
    return collect("META-INF/reflections/" , new FilterBuilder().include(".*-reflections.xml"));
  }


  public static Reflections collect(final String packagePrefix , final Predicate<String> resourceNameFilter , @Nullable Serializer... optionalSerializer) {
    Serializer serializer = optionalSerializer != null && optionalSerializer.length == 1 ? optionalSerializer[0] : new XmlSerializer();

    Collection<URL> urls = ClasspathHelper.forPackage(packagePrefix);
    if (urls.isEmpty()) return null;
    long               start       = System.currentTimeMillis();
    final Reflections  reflections = new Reflections();
    Iterable<Vfs.File> files       = Vfs.findFiles(urls , packagePrefix , resourceNameFilter);
    for (final Vfs.File file : files) {
      LOGGER.info("scanning VFS file : " + file.getRelativePath() + "/" + file.getName());
      InputStream inputStream = null;
      try {
        inputStream = file.openInputStream();
        reflections.merge(serializer.read(inputStream));
      } catch (IOException e) {
        throw new ReflectionsException("could not merge " + file , e);
      } finally {
        close(inputStream);
      }
    }

    if (LOGGER != null) {
      Store store = reflections.getStore();
      int keys = 0;
      int values = 0;
      for (String index : store.keySet()) {
        keys += store.get(index).keySet().size();
        values += store.get(index).size();
      }

      LOGGER.info(format("Reflections took %d ms to collect %d url%s, producing %d keys and %d values [%s]" ,
                         System.currentTimeMillis() - start , urls.size() , urls.size() > 1 ? "s" : "" , keys , values , Joiner.on(", ").join(urls)));
    }
    return reflections;
  }

  private static String index(Class<? extends Scanner> scannerClass) {
    return scannerClass.getSimpleName();
  }

  public Reflections merge(final Reflections reflections) {
    if (reflections.store != null) {
      for (String indexName : reflections.store.keySet()) {
        Multimap<String, String> index = reflections.store.get(indexName);
        for (String key : index.keySet()) {
          for (String string : index.get(key)) {
            store.getOrCreate(indexName).put(key , string);
          }
        }
      }
    }
    return this;
  }

  public Store getStore() {
    return store;
  }

  //
  protected void scan() {
    if (configuration.getUrls() == null || configuration.getUrls().isEmpty()) {
      if (LOGGER != null) LOGGER.warning("given scan urls are empty. set urls in the configuration");
      return;
    }

    if (LOGGER != null && LOGGER.isFineEnabled()) {
      LOGGER.finest("going to scan these urls:\n" + Joiner.on("\n").join(configuration.getUrls()));
    }

    long time = System.currentTimeMillis();
    int scannedUrls = 0;
    ExecutorService executorService = configuration.getExecutorService();
    List<Future<?>> futures = new ArrayList<>();

    for (final URL url : configuration.getUrls()) {
      try {
        if (executorService != null) {
          futures.add(executorService.submit(() -> {
            if (LOGGER != null && LOGGER.isFinestEnabled())
              LOGGER.finest("[" + Thread.currentThread().toString() + "] scanning " + url);
            scan(url);
          }));
        } else {
          scan(url);
        }
        scannedUrls++;
      } catch (ReflectionsException e) {
        if (LOGGER != null)
          LOGGER.warning("could not create Vfs.Dir from url. ignoring the exception and continuing" , e);
      }
    }

    //todo use CompletionService
    if (executorService != null) {
      for (Future future : futures) {
        try {
          future.get();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }

    time = System.currentTimeMillis() - time;

    //gracefully shutdown the parallel scanner executor service.
    if (executorService != null) {
      executorService.shutdown();
    }

    if (LOGGER != null) {
      int keys = 0;
      int values = 0;
      for (String index : store.keySet()) {
        keys += store.get(index).keySet().size();
        values += store.get(index).size();
      }

      LOGGER.info(format("Reflections took %d ms to scan %d urls, producing %d keys and %d values %s" ,
                         time , scannedUrls , keys , values ,
                         executorService != null && executorService instanceof ThreadPoolExecutor ?
                         format("[using %d cores]" , ((ThreadPoolExecutor) executorService).getMaximumPoolSize()) : ""));
    }
  }

  protected void scan(URL url) {
    Vfs.Dir dir = Vfs.fromURL(url);

    try {
      for (final Vfs.File file : dir.getFiles()) {
        // scan if inputs filter accepts file relative path or fqn
        Predicate<String> inputsFilter = configuration.getInputsFilter();
        String path = file.getRelativePath();
        String fqn = path.replace('/' , '.');
        if (inputsFilter == null || inputsFilter.apply(path) || inputsFilter.apply(fqn)) {
          Object classObject = null;
          for (Scanner scanner : configuration.getScanners()) {
            try {
              if (scanner.acceptsInput(path) || scanner.acceptResult(fqn)) {
                classObject = scanner.scan(file , classObject);
              }
            } catch (Exception e) {
              if (LOGGER != null && LOGGER.isFineEnabled())
                LOGGER.fine("could not scan file " + file.getRelativePath()
                            + " in url " + url.toExternalForm()
                            + " with scanner " + scanner.getClass().getSimpleName()
                            + e);
            }
          }
        }
      }
    } finally {
      dir.close();
    }
  }

  public Reflections collect(final File file) {
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);
      return collect(inputStream);
    } catch (FileNotFoundException e) {
      throw new ReflectionsException("could not obtain input stream from file " + file , e);
    } finally {
      Utils.close(inputStream);
    }
  }

  public Reflections collect(final InputStream inputStream) {
    try {
      merge(configuration.getSerializer().read(inputStream));
      if (LOGGER != null)
        LOGGER.info("Reflections collected metadata from input stream using serializer " + configuration.getSerializer().getClass().getName());
    } catch (Exception ex) {
      throw new ReflectionsException("could not merge input stream" , ex);
    }

    return this;
  }

  //query

  public void expandSuperTypes() {
    if (store.keySet().contains(index(SubTypesScanner.class))) {
      final Multimap<String, String> mmap = store.get(index(SubTypesScanner.class));
      final Collection<String> values = mmap.values();
      final Set<String> keys = Sets.difference(mmap.keySet() , new HashSet<>(values));
      final Multimap<String, String> expand = new MultimapImpl<>();
      for (final String key : keys) {
        expandSupertypes(expand , key , forName(key));
      }
      mmap.putAll(expand);
    }
  }

  private void expandSupertypes(Multimap<String, String> mmap , String key , Class<?> type) {
    for (Class<?> supertype : ReflectionUtils.getSuperTypes(type)) {
      if (mmap.put(supertype.getName() , key)) {
        if (LOGGER != null) LOGGER.fine("expanded subtype {} -> {} " + supertype.getName() + " - " + key);
        expandSupertypes(mmap , supertype.getName() , supertype);
      }
    }
  }

  public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
    final Iterable<String> all = store.getAll(index(SubTypesScanner.class) , Arrays.asList(type.getName()));
    return new HashSet<>(ReflectionUtils.<T>forNames(all , loaders()));
  }

  private ClassLoader[] loaders() {
    return configuration.getClassLoaders();
  }


  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
    return getTypesAnnotatedWith(annotation , false);
  }


  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation , boolean honorInherited) {
    Iterable<String> annotated = store.get(index(TypeAnnotationsScanner.class) , annotation.getName());
    Iterable<String> classes = getAllAnnotated(annotated , annotation.isAnnotationPresent(Inherited.class) , honorInherited);
    final List<Class<?>> classes1 = forNames(annotated , loaders());
    final List<Class<?>> classes2 = forNames(classes , loaders());

    final Iterable<Class<?>> concat = concat(classes1 , classes2);

    return StreamSupport
        .stream(concat.spliterator() , false)
        .collect(Collectors.toSet());
  }

  protected Iterable<String> getAllAnnotated(Iterable<String> annotated , boolean inherited , boolean honorInherited) {
    if (honorInherited) {
      if (inherited) {
        Iterable<String> subTypes = store.get(index(SubTypesScanner.class) , filter(annotated , (Predicate<String>) input -> ! ReflectionUtils.forName(input , loaders()).isInterface()));
        return concat(subTypes , store.getAll(index(SubTypesScanner.class) , subTypes));
      } else {
        return annotated;
      }
    } else {
      Iterable<String> subTypes = concat(annotated , store.getAll(index(TypeAnnotationsScanner.class) , annotated));
      return concat(subTypes , store.getAll(index(SubTypesScanner.class) , subTypes));
    }
  }


  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation) {
    return getTypesAnnotatedWith(annotation , false);
  }


  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation , boolean honorInherited) {
    final Iterable<String> annotated = store.get(index(TypeAnnotationsScanner.class) , annotation.annotationType().getName());
    final Iterable<Class<?>> filter = filter(forNames(annotated , loaders()) , withAnnotation(annotation));
    final Iterable<String> classes = getAllAnnotated(names(filter) , annotation.annotationType().isAnnotationPresent(Inherited.class) , honorInherited);
    final Iterable<Class<?>> concat = concat(
        filter ,
        forNames(
            filter(classes ,
                   not(
                       in(
                           StreamSupport
                               .stream(annotated.spliterator() , false)
                               .collect(Collectors.toSet())

                       ))) , loaders()));
    return StreamSupport
        .stream(concat.spliterator() , false)
        .collect(Collectors.toSet());
  }


  public Set<Method> getMethodsAnnotatedWith(final Annotation annotation) {
    return filter(getMethodsAnnotatedWith(annotation.annotationType()) , withAnnotation(annotation));
  }


  public Set<Method> getMethodsAnnotatedWith(final Class<? extends Annotation> annotation) {
    Iterable<String> methods = store.get(index(MethodAnnotationsScanner.class) , annotation.getName());
    return getMethodsFromDescriptors(methods , loaders());
  }


  public Set<Method> getMethodsMatchParams(Class<?>... types) {
    return getMethodsFromDescriptors(store.get(index(MethodParameterScanner.class) , names(types).toString()) , loaders());
  }


  public Set<Method> getMethodsReturn(Class returnType) {
    return getMethodsFromDescriptors(store.get(index(MethodParameterScanner.class) , names(returnType)) , loaders());
  }


  public Set<Method> getMethodsWithAnyParamAnnotated(Annotation annotation) {
    final Set<Method> methodsWithAnyParamAnnotated = getMethodsWithAnyParamAnnotated(annotation.annotationType());
    final Predicate<Member> memberPredicate = withAnyParameterAnnotation(annotation);
    return filter(methodsWithAnyParamAnnotated , (Predicate<Method>) memberPredicate::apply);
  }


  public Set<Method> getMethodsWithAnyParamAnnotated(Class<? extends Annotation> annotation) {
    return getMethodsFromDescriptors(store.get(index(MethodParameterScanner.class) , annotation.getName()) , loaders());

  }


  public Set<Constructor> getConstructorsAnnotatedWith(final Annotation annotation) {
    return filter(getConstructorsAnnotatedWith(annotation.annotationType()) , withAnnotation(annotation));
  }


  public Set<Constructor> getConstructorsAnnotatedWith(final Class<? extends Annotation> annotation) {
    Iterable<String> methods = store.get(index(MethodAnnotationsScanner.class) , annotation.getName());
    return getConstructorsFromDescriptors(methods , loaders());
  }


  public Set<Constructor> getConstructorsMatchParams(Class<?>... types) {
    return getConstructorsFromDescriptors(store.get(index(MethodParameterScanner.class) , names(types).toString()) , loaders());
  }


  public Set<Constructor> getConstructorsWithAnyParamAnnotated(Annotation annotation) {
    final Predicate<Member> memberPredicate = withAnyParameterAnnotation(annotation);
    final Set<Constructor> constructors = getConstructorsWithAnyParamAnnotated(annotation.annotationType());
    return filter(constructors , (Predicate<Constructor>) memberPredicate::apply);
  }


  public Set<Constructor> getConstructorsWithAnyParamAnnotated(Class<? extends Annotation> annotation) {
    return getConstructorsFromDescriptors(store.get(index(MethodParameterScanner.class) , annotation.getName()) , loaders());
  }


  public Set<Field> getFieldsAnnotatedWith(final Annotation annotation) {
    return filter(getFieldsAnnotatedWith(annotation.annotationType()) , withAnnotation(annotation));
  }


  public Set<Field> getFieldsAnnotatedWith(final Class<? extends Annotation> annotation) {
    final Set<Field> result = new HashSet<>();
    for (String annotated : store.get(index(FieldAnnotationsScanner.class) , annotation.getName())) {
      result.add(getFieldFromString(annotated , loaders()));
    }
    return result;
  }


  public Set<String> getResources(final Pattern pattern) {
    return getResources(input -> pattern.matcher(input).matches());
  }


  public Set<String> getResources(final Predicate<String> namePredicate) {
    Iterable<String> resources = store.get(index(ResourcesScanner.class)).keySet()
                                      .stream()
                                      .filter(namePredicate::apply)
                                      .collect(Collectors.toList()
                                      );
    Iterable<String> resourceStrings = store.get(index(ResourcesScanner.class) , resources);
    Stream<String> resourceStringStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(resourceStrings.iterator() , Spliterator.ORDERED) , false);

    return resourceStringStream.collect(Collectors.toSet());
  }


  public List<String> getMethodParamNames(Method method) {
    Iterable<String> names = store.get(index(MethodParameterNamesScanner.class) , name(method));
    return ! Iterables.isEmpty(names) ? Arrays.asList(Iterables.getOnlyElement(names).split(", ")) : Arrays.asList();
  }


  public List<String> getConstructorParamNames(Constructor constructor) {
    Iterable<String> names = store.get(index(MethodParameterNamesScanner.class) , Utils.name(constructor));
    return ! Iterables.isEmpty(names) ? Arrays.asList(Iterables.getOnlyElement(names).split(", ")) : Arrays.asList();
  }


  public Set<Member> getFieldUsage(Field field) {
    return getMembersFromDescriptors(store.get(index(MemberUsageScanner.class) , name(field)));
  }


  public Set<Member> getMethodUsage(Method method) {
    return getMembersFromDescriptors(store.get(index(MemberUsageScanner.class) , name(method)));
  }


  public Set<Member> getConstructorUsage(Constructor constructor) {
    return getMembersFromDescriptors(store.get(index(MemberUsageScanner.class) , name(constructor)));
  }


  public Set<String> getAllTypes() {
    final String index = index(SubTypesScanner.class);
    final Iterable<String> storeAll = store.getAll(index , Object.class.getName());
    final Set<String> allTypes = StreamSupport
        .stream(storeAll.spliterator() , false)
        .collect(Collectors.toSet());

    if (allTypes.isEmpty()) {
      throw new ReflectionsException("Couldn't find subtypes of Object. " +
                                     "Make sure SubTypesScanner initialized to include Object class - new SubTypesScanner(false)");
    }
    return allTypes;
  }


  public Configuration getConfiguration() {
    return configuration;
  }


  public File save(final String filename) {
    return save(filename , configuration.getSerializer());
  }


  public File save(final String filename , final Serializer serializer) {
    File file = serializer.save(this , filename);
    if (LOGGER != null) //noinspection ConstantConditions
      LOGGER.info("Reflections successfully saved in " + file.getAbsolutePath() + " using " + serializer.getClass().getSimpleName());
    return file;
  }
}
