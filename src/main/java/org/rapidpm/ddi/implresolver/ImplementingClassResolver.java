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

package org.rapidpm.ddi.implresolver;

import org.jetbrains.annotations.Nullable;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.proxybuilder.objectadapter.annotations.staticobjectadapter.IsStaticObjectAdapter;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsGeneratedProxy;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsMetricsProxy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.rapidpm.ddi.producer.ProducerLocator.findProducersFor;

/**
 * one subtype - will return this class
 * n subtypes - will search for classresolver to decide what will be the right implementation
 * <p>
 * no subtype - will return the interface itself, maybe on the fly implementations are available
 * <p>
 * Created by svenruppert on 23.07.15.
 */
public class ImplementingClassResolver {

  private static final ImplementingClassResolver INSTANCE = new ImplementingClassResolver();


  //here only if you have 1 interface and multiple implementations - here the ClassResolver
//  private final Map<Class, Class> resolverCacheForClass2Class = new ConcurrentHashMap<>();
  private final Map<Class, Class<? extends ClassResolver>> resolverCacheForClass2ClassResolver = new ConcurrentHashMap<>();

  public static void clearCache() {
//    INSTANCE.resolverCacheForClass2Class.clear();
    INSTANCE.resolverCacheForClass2ClassResolver.clear();
  }


  public static <I> Class<? extends I> resolve(Class<I> interf) {
    return INSTANCE.resolveNewForClass(interf);
  }

  private <I> Class<? extends I> resolveNewForClass(final Class<I> interf) {
    if (interf.isInterface()) {
      final Set<Class<? extends I>> subTypesOf = DI.getSubTypesOf(interf);

      //remove subtypes that are interfaces
      removeInterfacesFromSubTypes(subTypesOf);

      if (subTypesOf.isEmpty()) return interf;
      else if (subTypesOf.size() == 1) {
        return handleOneSubType(interf, subTypesOf.toArray()[0]);
      } else {
        return handleManySubTypes(interf, subTypesOf);
      }
    } else {
      return interf;
    }
  }

  private <I> void removeInterfacesFromSubTypes(final Set<Class<? extends I>> subTypesOf) {
    final Iterator<Class<? extends I>> iteratorOfSubTypes = subTypesOf.iterator();
    while (iteratorOfSubTypes.hasNext()) {
      final Class<? extends I> next = iteratorOfSubTypes.next();
      if (next.isInterface()
          || next.isAnnotationPresent(IsStaticObjectAdapter.class)
          || next.isAnnotationPresent(IsMetricsProxy.class)
          || next.isAnnotationPresent(IsGeneratedProxy.class)
          ) iteratorOfSubTypes.remove();
    }

  }

  //ToDo this result could be cached....
  @Nullable
  private <I> Class<? extends I> handleOneSubType(final Class<I> interf, final Object o) {
    final Class<? extends I> implClass = (Class<? extends I>) o;
    final Set<Class<?>> producersForInterface = findProducersFor(interf);
    final Set<Class<?>> producersForImpl = findProducersFor(implClass);
    //@formatter:off
        if (!producersForInterface.isEmpty() && !producersForImpl.isEmpty()) return interf;
        if (producersForInterface.isEmpty()  && producersForImpl.isEmpty())  return implClass;
        if (producersForImpl.isEmpty())                                      return interf;
        if (producersForInterface.isEmpty())                                 return implClass;
        //@formatter:on
    return null;
  }

  @Nullable
  private <I> Class<? extends I> handleManySubTypes(final Class<I> interf, final Set<Class<? extends I>> subTypesOf) {

    if (resolverCacheForClass2ClassResolver.containsKey(interf)) {
      return handleOneResolver(interf, resolverCacheForClass2ClassResolver.get(interf));
    }

    final List<Class<? extends ClassResolver>> clearedListOfResolvers = DI.getSubTypesOf(ClassResolver.class)
        .stream()
        .filter(aClassResolver -> aClassResolver.isAnnotationPresent(ResponsibleFor.class))
        .filter(aClassResolver -> {
          final ResponsibleFor responsibleFor = aClassResolver.getAnnotation(ResponsibleFor.class);
          return interf.equals(responsibleFor.value());
        })
        .collect(Collectors.toList());

    if (clearedListOfResolvers.size() == 1) {
      final Class<? extends ClassResolver> classResolverClass = clearedListOfResolvers.get(0);
      resolverCacheForClass2ClassResolver.put(interf, classResolverClass);
      return handleOneResolver(interf, classResolverClass);
    } else if (clearedListOfResolvers.isEmpty()) {
      return handleNoResolvers(interf, subTypesOf);
    }

    final String message = "interface with multiple implementations and more as 1 ClassResolver = "
        + interf
        + " ClassResolver: " + clearedListOfResolvers;
    throw new DDIModelException(message);
  }

  @Nullable
  private <I> Class<? extends I> handleOneResolver(final Class<I> interf, final Class<? extends ClassResolver> classResolverClass) {
    try {
      final ClassResolver<I> classResolver = classResolverClass.newInstance();
      return classResolver.resolve(interf);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      throw new DDIModelException(interf + " -- " + e);
    }
  }

  private <I> Class<? extends I> handleNoResolvers(final Class<I> interf, final Set<Class<? extends I>> subTypesOf) {
    final Set<Class<?>> producersForInterface = findProducersFor(interf);
    if (producersForInterface.isEmpty()) {
      final StringBuilder stringBuilder = new StringBuilder("interface with multiple implementations and no ClassResolver= " + interf);
      final List<String> implList = subTypesOf
          .stream()
          .map(c -> "impl. : " + c.getName()).collect(Collectors.toList());
      stringBuilder.append(implList);

      throw new DDIModelException(stringBuilder.toString());
    }
    if (producersForInterface.size() == 1) return interf;

    final StringBuilder stringBuilder = new StringBuilder("interface with multiple implementations and no ClassResolver and n Producers f the interface = " + interf);

    final List<String> implList = subTypesOf.stream().map(c -> "impl. : " + c.getName()).collect(Collectors.toList());
    final List<String> prodList = producersForInterface.stream().map(c -> "producer. : " + c.getName()).collect(Collectors.toList());

    stringBuilder.append(implList).append(prodList);

    throw new DDIModelException(stringBuilder.toString());
  }

}

