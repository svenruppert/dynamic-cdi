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

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Set;

import org.rapidpm.dependencies.core.stream.ImmutableSetCollector;

public class ReflectionUtils extends org.reflections.ReflectionUtils {

  public boolean checkInterface(final Type aClass , Class targetInterface) {
    if (aClass.equals(targetInterface)) return true;

    final Type[] genericInterfaces = ((Class) aClass).getGenericInterfaces();
    for (final Type genericInterface : genericInterfaces) {
      if (genericInterface.equals(targetInterface)) return true;
      final Type[] nextLevBackArray = ((Class) genericInterface).getGenericInterfaces();
      for (final Type type : nextLevBackArray) {
        if (checkInterface(type , targetInterface)) return true;
      }
    }
    final Type genericSuperclass = ((Class) aClass).getGenericSuperclass();
    if (genericSuperclass != null) {
      if (checkInterface(genericSuperclass , targetInterface)) return true;
    }
    return false;
  }

  public <T> Set<Class<? extends T>> removeInterfacesAndGeneratedFromSubTypes(final Set<Class<? extends T>> subTypesOf) {
    return subTypesOf
        .stream()
        .filter((c) -> ! (c.isInterface()))
        .filter((c) -> ! (Modifier.isAbstract(c.getModifiers())))
        .collect(ImmutableSetCollector.toImmutableSet());
  }

}
