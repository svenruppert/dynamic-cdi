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

import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.proxybuilder.objectadapter.annotations.staticobjectadapter.IsStaticObjectAdapter;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsGeneratedProxy;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsLoggingProxy;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsMetricsProxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class ReflectionUtils extends org.reflections.ReflectionUtils {

  public boolean checkInterface(final Type aClass, Class targetInterface) {
    if (aClass.equals(targetInterface)) return true;

    final Type[] genericInterfaces = ((Class) aClass).getGenericInterfaces();
//    if (genericInterfaces.length > 0) {
    for (final Type genericInterface : genericInterfaces) {
      if (genericInterface.equals(targetInterface)) return true;

      final Type[] nextLevBackArray = ((Class) genericInterface).getGenericInterfaces();
      //if (nextLevBackArray.length > 0)
      for (final Type type : nextLevBackArray) {
        if (checkInterface(type, targetInterface)) return true;
      }
    }
//    }
    final Type genericSuperclass = ((Class) aClass).getGenericSuperclass();
    if (genericSuperclass != null) {
      if (checkInterface(genericSuperclass, targetInterface)) return true;
    }


    return false;
  }


  public <T, P extends T, O extends T> void setDelegatorToProxy(P proxy, O original) {
    try {
      final Class<?> proxyClass = proxy.getClass();
      final Field delegator = proxyClass.getDeclaredField("delegator");
      final boolean accessible = delegator.isAccessible();
      delegator.setAccessible(true);
      // TODO how to deal with lambdas ?
      delegator.set(proxy, original);

      delegator.setAccessible(accessible);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new DDIModelException(e);
    }
  }

  public <T> void setDelegatorToProxyViaMethod(T proxy, T original) {
    final Class<?> proxyClass = proxy.getClass();
    final Optional<Method> methodOptional = Arrays
        .stream(proxyClass.getDeclaredMethods())
        .filter(m -> m.getName().equals("withDelegator"))
        .findFirst();

    if (methodOptional.isPresent()) {
      final Method method = methodOptional.get();
      try {
        method.invoke(proxy, original);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new DDIModelException(e);
      }
    }
  }


  public <I> void removeInterfacesAndGeneratedFromSubTypes(final Set<Class<? extends I>> subTypesOf) {
    final Iterator<Class<? extends I>> iteratorOfSubTypes = subTypesOf.iterator();
    while (iteratorOfSubTypes.hasNext()) {
      final Class<? extends I> next = iteratorOfSubTypes.next();
      if (next.isInterface()
          || next.isAnnotationPresent(IsStaticObjectAdapter.class)
          || next.isAnnotationPresent(IsGeneratedProxy.class)
          || next.isAnnotationPresent(IsMetricsProxy.class)
          || next.isAnnotationPresent(IsLoggingProxy.class)
          ) iteratorOfSubTypes.remove();
    }

  }


}
