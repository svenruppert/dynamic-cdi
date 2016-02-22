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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

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


  public <T> void setDelegatorToMetrixsProxy(T proxy, T original) {

    final String simpleName = original.getClass().getSimpleName();
    try {
//      final Method declaredMethod = proxy.getClass().getDeclaredMethod("with" + simpleName, original.getClass());
      final Method declaredMethod = proxy.getClass().getDeclaredMethod("withDelegator", original.getClass());
      declaredMethod.invoke(proxy, original);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      throw new DDIModelException(e);
    }

  }


}
