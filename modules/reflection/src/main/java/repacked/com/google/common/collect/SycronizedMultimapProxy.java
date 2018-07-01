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
package repacked.com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by benjamin-bosch on 07.11.16.
 */
public class SycronizedMultimapProxy<KEY, VALUES> implements Multimap<KEY, VALUES> {

  private final Multimap<KEY, VALUES> delegate;
  private final Object                mutex = new Object();

  public SycronizedMultimapProxy(Multimap<KEY, VALUES> multimap) {
    this.delegate = multimap;
  }


  @Override
  public boolean put(@Nullable KEY key , @Nullable VALUES value) {
    synchronized (mutex) {
      return delegate.put(key , value);
    }
  }

  @Override
  public boolean isEmpty() {
    synchronized (mutex) {
      return delegate.isEmpty();
    }
  }

  @Override
  public Set<KEY> keySet() {
    synchronized (mutex) {
      return delegate.keySet();
    }
  }

  @Override
  public Iterable<? extends Map.Entry<KEY, VALUES>> entries() {
    synchronized (mutex) {
      return delegate.entries();
    }
  }

  @Override
  public Collection<VALUES> get(KEY key) {
    synchronized (mutex) {
      return delegate.get(key);
    }
  }

  @Override
  public int size() {
    synchronized (mutex) {
      return delegate.size();
    }
  }

  @Override
  public Collection<VALUES> values() {
    synchronized (mutex) {
      return delegate.values();
    }
  }

  @Override
  public boolean putAll(Multimap<KEY, VALUES> multimap) {
    synchronized (mutex) {
      return delegate.putAll(multimap);
    }
  }

  @Override
  public boolean putAll(@Nullable KEY key , Iterable<? extends VALUES> values) {
    synchronized (mutex) {
      return delegate.putAll(key , values);
    }
  }

  @Override
  public Map<KEY, Collection<VALUES>> asMap() {
    synchronized (mutex) {
      return delegate.asMap();
    }
  }
}
