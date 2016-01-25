package org.rapidpm.ddi.scopes.provided;

import org.rapidpm.ddi.scopes.InjectionScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Sven Ruppert on 20.01.16.
 */
public class JVMSingletonInjectionScope extends InjectionScope {

  private static final Map<String, Object> SINGLETONS = new ConcurrentHashMap<>();

  @Override
  public <T> T getInstance(final String clazz) {
    if (SINGLETONS.containsKey(clazz)) {
      return (T) SINGLETONS.get(clazz);
    }
    return null;
  }

  @Override
  public <T> void storeInstance(final Class<T> targetClassOrInterface, final T instance) {
    final String name = targetClassOrInterface.getName();
    if (SINGLETONS.containsKey(name)) {
      //logging that Singleton was tried to set twice
    } else {
      SINGLETONS.put(name, instance);
    }

  }

  @Override
  public void clear() {
    SINGLETONS.clear();
  }

  @Override
  public String getScopeName() {
    return JVMSingletonInjectionScope.class.getSimpleName();
  }
}
