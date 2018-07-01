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
package org.reflections;

import repacked.com.google.common.collect.Iterables;
import repacked.com.google.common.collect.Multimap;
import repacked.com.google.common.collect.Multimaps;

import java.util.*;

/**
 * stores metadata information in multimaps
 * <p>use the different query methods (getXXX) to query the metadata
 * <p>the query methods are string based, and does not cause the class loader to define the types
 * <p>use {@link org.reflections.Reflections#getStore()} to access this store
 */
public class Store {

  private final Map<String, Multimap<String, String>> storeMap;
  private transient boolean                           concurrent;

  //used via reflection
  @SuppressWarnings("UnusedDeclaration")
  protected Store() {
    storeMap = new HashMap<>();
    concurrent = false;
  }

  public Store(Configuration configuration) {
    storeMap = new HashMap<>();
    concurrent = configuration.getExecutorService() != null;
  }


  public Set<String> keySet() {
    return storeMap.keySet();
  }


  public Multimap<String, String> getOrCreate(String index) {
    Multimap<String, String> mmap = storeMap.get(index);
    if (mmap == null) {
      Multimap<String, String> multimap =
          Multimaps.newSetMultimap(new HashMap<String, Collection<String>>() , HashSet::new);
      mmap = concurrent ? Multimaps.synchronizedSetMultimap(multimap) : multimap;
      storeMap.put(index , mmap);
    }
    return mmap;
  }


  public Multimap<String, String> get(String index) {
    Multimap<String, String> mmap = storeMap.get(index);
    if (mmap == null) {
      throw new ReflectionsException("Scanner " + index + " was not configured");
    }
    return mmap;
  }


  public Iterable<String> get(String index , String... keys) {
    return get(index , Arrays.asList(keys));
  }


  public Iterable<String> get(String index , Iterable<String> keys) {
    Multimap<String, String> mmap   = get(index);
    IterableChain<String>    result = new IterableChain<>();
    for (String key : keys) {
      result.addAll(mmap.get(key));
    }
    return result;
  }


  private Iterable<String> getAllIncluding(String index , Iterable<String> keys , IterableChain<String> result) {
    result.addAll(keys);
    for (String key : keys) {
      Iterable<String> values = get(index , key);
      if (values.iterator().hasNext()) {
        getAllIncluding(index , values , result);
      }
    }
    return result;
  }


  public Iterable<String> getAll(String index , String key) {
    return getAllIncluding(index , get(index , key) , new IterableChain<>());
  }


  public Iterable<String> getAll(String index , Iterable<String> keys) {
    return getAllIncluding(index , get(index , keys) , new IterableChain<>());
  }

  private static class IterableChain<T> implements Iterable<T> {
    private final List<Iterable<T>> chain = new ArrayList<>();

    private void addAll(Iterable<T> iterable) {
      chain.add(iterable);
    }

    public Iterator<T> iterator() {
      return Iterables.concat(chain).iterator();
    }
  }
}
