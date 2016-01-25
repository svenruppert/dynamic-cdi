package org.rapidpm.ddi.scopes;

import org.rapidpm.ddi.DI;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Created by Sven Ruppert on 20.01.16.
 */
public class InjectionScopeManager {

  private static final Map<String, String> CLASS_NAME_2_SCOPENAME_MAP;
  private static final Map<String, InjectionScope> INJECTION_SCOPE_MAP;

  static {
    CLASS_NAME_2_SCOPENAME_MAP = new ConcurrentHashMap<>();
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

  public static void cleanUp() {
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

    activeScopeNames.stream()
        .forEach(InjectionScopeManager::removeScope);
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

  public static void removeScope(final String scopeName) {
    INJECTION_SCOPE_MAP.computeIfPresent(scopeName, (s, injectionScope) -> {
      injectionScope.clear();
      return injectionScope;
    });
    if (INJECTION_SCOPE_MAP.containsKey(scopeName)) {
      INJECTION_SCOPE_MAP.remove(scopeName);
    }
  }
}
