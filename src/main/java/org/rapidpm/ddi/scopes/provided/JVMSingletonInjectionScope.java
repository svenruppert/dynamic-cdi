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
package org.rapidpm.ddi.scopes.provided;

import org.rapidpm.ddi.scopes.InjectionScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
      throw new RuntimeException("tried to set the Singleton twice .. " + targetClassOrInterface + " with instance " + instance);
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
