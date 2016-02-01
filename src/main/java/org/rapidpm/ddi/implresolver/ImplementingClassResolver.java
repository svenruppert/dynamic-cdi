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
import org.rapidpm.ddi.producer.ProducerLocator;
import org.rapidpm.proxybuilder.objectadapter.annotations.staticobjectadapter.IsStaticObjectAdapter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * one subtype - will return this class
 * n subtypes - will search for classresolver to decide what will be the right implementation
 * <p>
 * no subtype - will return the interface itself, maybe on the fly implementations are available
 * <p>
 * Created by svenruppert on 23.07.15.
 */
//public class ImplementingClassResolver<I> implements ClassResolver<I> {
public class ImplementingClassResolver {

  private final Map<Class, Class> resolverCache = new ConcurrentHashMap<>();

  public void clearCache() {
    resolverCache.clear();
  }


  public <I> Class<? extends I> resolve(Class<I> interf) {
    if (!resolverCache.containsKey(interf)) {
      resolverCache.put(interf, resolveNewForClass(interf));
    }
    return resolverCache.get(interf);
//    return resolveNewForClass(interf);
  }

  private <I> Class<? extends I> resolveNewForClass(final Class<I> interf) {
    if (interf.isInterface()) {
      final Set<Class<? extends I>> subTypesOf = DI.getSubTypesOf(interf);

      //remove subtypes that are interfaces
      removeInterfacesFromSubTypes(subTypesOf);
      if (subTypesOf.isEmpty()) return interf;
      else if (subTypesOf.size() == 1) {
        final Class<? extends I> implClass = handleOneSubType(interf, subTypesOf.toArray()[0]);
        if (implClass != null) return implClass;
      } else {
        final Class<? extends I> clearedListOfResolvers = handleManySubTypes(interf, subTypesOf);
        if (clearedListOfResolvers != null) return clearedListOfResolvers;
      }
    } else {
      return interf;
    }
    throw new DDIModelException("this point should never been reached.. no decission possible for = " + interf);
  }

  private <I> void removeInterfacesFromSubTypes(final Set<Class<? extends I>> subTypesOf) {
    final Iterator<Class<? extends I>> iteratorOfSubTypes = subTypesOf.iterator();
    while (iteratorOfSubTypes.hasNext()) {
      final Class<? extends I> next = iteratorOfSubTypes.next();
      if (next.isInterface()) iteratorOfSubTypes.remove();
      //remove Adapters -  http://rapidpm.myjetbrains.com/youtrack/issue/DDI-5
      //DDI-5
      if (next.isAnnotationPresent(IsStaticObjectAdapter.class)) iteratorOfSubTypes.remove();

    }
  }

  @Nullable
  private <I> Class<? extends I> handleOneSubType(final Class<I> interf, final Object o) {
    final Class<? extends I> implClass = (Class<? extends I>) o;
    final Set<Class<?>> producersForInterface = new ProducerLocator().findProducersFor(interf);
    final Set<Class<?>> producersForImpl = new ProducerLocator().findProducersFor(implClass);
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
    final Set<Class<? extends ClassResolver>> subTypesOfClassResolver = DI.getSubTypesOf(ClassResolver.class);
    final List<Class> clearedListOfResolvers = subTypesOfClassResolver
        .stream()
        .filter(aClassResolver -> aClassResolver.isAnnotationPresent(ResponsibleFor.class))
        .filter(aClassResolver -> {
          final ResponsibleFor responsibleFor = aClassResolver.getAnnotation(ResponsibleFor.class);
          return interf.equals(responsibleFor.value());
        })
        .collect(Collectors.toList());

    if (clearedListOfResolvers.size() == 1) {
      final Class<? extends I> classResolver = handleOneResolver(interf, clearedListOfResolvers);
      if (classResolver != null) return classResolver;
    } else if (clearedListOfResolvers.isEmpty()) {
      return handleNoResolvers(interf, subTypesOf);
    } else {
      return handleToManyResolvers(interf, clearedListOfResolvers);
    }
    return null;
  }

  @Nullable
//  private <I> Class<? extends I> handleOneResolver(final Class<I> interf, final List<Class<ClassResolver<I>>> clearedListOfResolvers) {
  private <I> Class<? extends I> handleOneResolver(final Class<I> interf, final List<Class> clearedListOfResolvers) {
    final Class<ClassResolver<I>> resolver = clearedListOfResolvers.get(0);
    try {
      final ClassResolver<I> classResolver = resolver.newInstance();
      return classResolver.resolve(interf);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      throw new DDIModelException(interf + " -- " + e);
    }
  }

  private <I> Class<? extends I> handleNoResolvers(final Class<I> interf, final Set<Class<? extends I>> subTypesOf) {
    final Set<Class<?>> producersForInterface = new ProducerLocator().findProducersFor(interf);
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

  private <I> Class<? extends I> handleToManyResolvers(final Class<I> interf, final List<Class> clearedListOfResolvers) {
    final String message = "interface with multiple implementations and more as 1 ClassResolver = "
        + interf
        + " ClassResolver: " + clearedListOfResolvers;
    throw new DDIModelException(message);
  }

}

