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
package com.svenruppert.ddi.scopes;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.svenruppert.ddi.DI;
import com.svenruppert.dependencies.core.logger.Logger;
import com.svenruppert.dependencies.core.logger.LoggingService;


public class InjectionScopeManager {


  private static final LoggingService LOGGER = Logger.getLogger(InjectionScopeManager.class);
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


  public static <T> void manageInstance(Class<T> targetClass , T instance) {
    final String targetName = targetClass.getName();
    if (CLASS_NAME_2_SCOPENAME_MAP.containsKey(targetName)) {
      final InjectionScope injectionScope = INJECTION_SCOPE_MAP.get(CLASS_NAME_2_SCOPENAME_MAP.get(targetName));
      injectionScope.storeInstance(targetClass , instance);
    }
  }

  public static boolean isManagedByMe(Class clazz) {
    return CLASS_NAME_2_SCOPENAME_MAP.containsKey(clazz.getName());
  }

  public static synchronized void cleanUp() {
    final Set<Class<? extends InjectionScope>> scopesFromReflectionModel = DI.getSubTypesOf(InjectionScope.class);
    registerNewScopes(scopesFromReflectionModel);
    removeOldScopes(scopesFromReflectionModel);
  }

  private static void registerNewScopes(Set<Class<? extends InjectionScope>> scopeClasses) {
    scopeClasses
        .stream()
        .map(c -> {
          try {
            return c.getDeclaredConstructor().newInstance();
          } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.warning("could not create an instance " , e);
          }
          return null;
        })
        .filter(Objects::nonNull)
        .filter(scope -> ! INJECTION_SCOPE_MAP.containsKey(scope.getScopeName()))
        .forEach((injectionScope) -> INJECTION_SCOPE_MAP.put(injectionScope.getScopeName() , injectionScope));
  }

  private static void removeOldScopes(Set<Class<? extends InjectionScope>> scopeClasses) {

    final Set<String> scopeNamesFromReflectionModel = getNamesFromScopes(scopeClasses);

    INJECTION_SCOPE_MAP.keySet().stream()
                       .filter(scope -> ! scopeNamesFromReflectionModel.contains(scope))
                       .forEach(InjectionScopeManager::removeScope);
  }

  private static Set<String> getNamesFromScopes(Set<Class<? extends InjectionScope>> scopes) {
    return scopes.stream()
                 .map(c -> {
                   try { //TODO CheckedFunction
                     return c.getDeclaredConstructor().newInstance();
                   } catch (InstantiationException | IllegalAccessException
                       | NoSuchMethodException | InvocationTargetException e) {
                     LOGGER.warning("could not create new instance " , e);
                   }
                   return null;
                 })
                 .filter(Objects::nonNull)
                 .map(InjectionScope::getScopeName)
                 .collect(Collectors.toSet());
  }

  public static void registerClassForScope(final Class clazz , final String scopeName) {
    if (INJECTION_SCOPE_MAP.containsKey(scopeName)) {
      CLASS_NAME_2_SCOPENAME_MAP.putIfAbsent(clazz.getName() , scopeName);
    }
  }

  public static void deRegisterClassForScope(final Class clazz) {
    if (CLASS_NAME_2_SCOPENAME_MAP.containsKey(clazz.getName())) {
      CLASS_NAME_2_SCOPENAME_MAP.remove(clazz.getName());
    }
  }

  public static String scopeForClass(final Class clazz) {
    final String clazzName = clazz.getName();
    return CLASS_NAME_2_SCOPENAME_MAP.getOrDefault(clazzName , "PER INJECT");
  }

  public static Set<String> listAllActiveScopeNames() {
    return Collections.unmodifiableSet(INJECTION_SCOPE_MAP.keySet());
  }

  public static void clearScope(final String scopeName) {
    INJECTION_SCOPE_MAP.computeIfPresent(scopeName , (s , injectionScope) -> {
      injectionScope.clear();
      return injectionScope;
    });
  }


  private static void removeScope(final String scopeName) {
    final Set<String> keySet = CLASS_NAME_2_SCOPENAME_MAP.keySet();
    INJECTION_SCOPE_MAP
        .computeIfPresent(scopeName , (s , injectionScope) -> {
          injectionScope.clear();
          keySet.forEach(k -> CLASS_NAME_2_SCOPENAME_MAP
              .computeIfPresent(k , (classname , scope) -> (scope.equals(scopeName)) ? null : scope));
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
        final InjectionScope injectionScope = aClass.getDeclaredConstructor().newInstance();
        INJECTION_SCOPE_MAP.put(injectionScope.getScopeName() , injectionScope);
      } catch (InstantiationException | IllegalAccessException
          | NoSuchMethodException | InvocationTargetException e) {
        LOGGER.warning("could not create an instance " , e);
      }
    }

  }
}
