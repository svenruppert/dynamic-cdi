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
public interface Multimap<KEY, VALUES> {
  boolean put(@Nullable KEY key, @Nullable VALUES value);

  boolean isEmpty();

  Set<KEY> keySet();

  Iterable<? extends Map.Entry<KEY, VALUES>> entries();

  Collection<VALUES> get(KEY key);

  int size();

  Collection<VALUES> values();

  boolean putAll(Multimap<KEY, VALUES> multimap);

  boolean putAll(@Nullable KEY key, Iterable<? extends VALUES> values);

  Map<KEY, Collection<VALUES>> asMap();
}
