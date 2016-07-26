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
 * under the License.    CLASS_NAME_2_SCOPENAME_MAP = new ConcurrentHashMap<>();
    INJECTION_SCOPE_MAP = new ConcurrentHashMap<>();
    final Set<Class<? extends InjectionScope>> subTypesOf = DI.getSubTypesOf(InjectionScope.class);
    for (Class<? extends InjectionScope> aClass : subTypesOf) {
      try {
        final InjectionScope injectionScope = aClass.newInstance();
        INJECTION_SCOPE_MAP.put(injectionScope.getScopeName(), injectionScope);
      } catch (InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }

 */

package org.rapidpm.ddi.scopes;

import org.rapidpm.ddi.DI;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class InjectionScopeManager {


  private static final Map<String, String> CLASS_NAME_2_SCOPENAME_MAP = new ConcurrentHashMap<>();
  private static final Map<String, InjectionScope> INJECTION_SCOPE_MAP = new ConcurrentHashMap<>();

  static {
    reInitAllScopes();
  }

  private InjectionScopeManager() {

  }

  public static <T> T getInstance(final Class<T> target) {
    final String targetName = target.getName();

    if (CLASS_NAME_2_SCOPENAME_MAP.containsKey(targetName)) {
      final InjectionScope injectionScope = INJECTION_SCOPE_MAP.get(CLASS_NAME_2_SCOPENAME_MAP.get(targetName));
      return injectionScope.getInstance(targetName);
    }
    return null;
  }


  public static <T> void manageInstance(Class<T> targetClass, T instance) {
    final String targetName = targetClass.getName();
    if (CLASS_NAME_2_SCOPENAME_MAP.containsKey(targetName)) {
      final InjectionScope injectionScope = INJECTION_SCOPE_MAP.get(CLASS_NAME_2_SCOPENAME_MAP.get(targetName));
      injectionScope.storeInstance(targetClass, instance);
    }
  }

  public static boolean isManagedByMe(Class clazz) {
    return CLASS_NAME_2_SCOPENAME_MAP.containsKey(clazz.getName());
  }

  public static synchronized void cleanUp() {
    final Set<Class<? extends InjectionScope>> subTypesOf = DI.getSubTypesOf(InjectionScope.class);
    subTypesOf
            .stream()
            .map(c -> {
              try {
                return c.newInstance();
              } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
              }
              return null;
            })
            .filter(scope -> scope != null)
            .filter(scope -> !INJECTION_SCOPE_MAP.containsKey(scope.getScopeName()))
            .forEach((injectionScope) -> INJECTION_SCOPE_MAP.put(injectionScope.getScopeName(), injectionScope));

    final Set<String> activeScopeNames = new HashSet<>(INJECTION_SCOPE_MAP.keySet());

    final Set<String> scopes = subTypesOf.stream()
            .map(c -> {
              try {
                return c.newInstance();
              } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
              }
              return null;
            })
            .filter(scope -> scope != null)
            .map(InjectionScope::getScopeName)
            .collect(Collectors.toSet());

    activeScopeNames.removeAll(scopes);

    // remove all non existing scope instances
    activeScopeNames.forEach(InjectionScopeManager::removeScope);
  }

  public static void registerClassForScope(final Class clazz, final String scopeName) {
    if (INJECTION_SCOPE_MAP.containsKey(scopeName)) {
      CLASS_NAME_2_SCOPENAME_MAP.putIfAbsent(clazz.getName(), scopeName);
    }
  }

  public static void deRegisterClassForScope(final Class clazz) {
    if (CLASS_NAME_2_SCOPENAME_MAP.containsKey(clazz.getName())) {
      CLASS_NAME_2_SCOPENAME_MAP.remove(clazz.getName());
    }
  }

  public static String scopeForClass(final Class clazz) {
    final String clazzName = clazz.getName();
    if (CLASS_NAME_2_SCOPENAME_MAP.containsKey(clazzName)) {
      return CLASS_NAME_2_SCOPENAME_MAP.get(clazzName);
    } else return "PER INJECT";
  }

  public static Set<String> listAllActiveScopeNames() {
    return Collections.unmodifiableSet(INJECTION_SCOPE_MAP.keySet());
  }

  public static void clearScope(final String scopeName) {
    INJECTION_SCOPE_MAP.computeIfPresent(scopeName, (s, injectionScope) -> {
      injectionScope.clear();
      return injectionScope;
    });
  }


  private static void removeScope(final String scopeName) {
    final Set<String> keySet = CLASS_NAME_2_SCOPENAME_MAP.keySet();
    INJECTION_SCOPE_MAP
            .computeIfPresent(scopeName, (s, injectionScope) -> {
              injectionScope.clear();
              keySet.forEach(k -> CLASS_NAME_2_SCOPENAME_MAP
                      .computeIfPresent(k, (classname, scope) -> (scope.equals(scopeName)) ? null : scope));
              return null;
            });
  }

  public static void reInitAllScopes() {
    CLASS_NAME_2_SCOPENAME_MAP.clear();
    INJECTION_SCOPE_MAP.values().forEach(InjectionScope::clear);
    INJECTION_SCOPE_MAP.clear();
    final Set<Class<? extends InjectionScope>> subTypesOf = DI.getSubTypesOf(InjectionScope.class);
    for (Class<? extends InjectionScope> aClass : subTypesOf) {
      try {
        final InjectionScope injectionScope = aClass.newInstance();
        INJECTION_SCOPE_MAP.put(injectionScope.getScopeName(), injectionScope);
      } catch (InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }

  }
}
