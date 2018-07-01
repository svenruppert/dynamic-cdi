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
package org.reflections.scanners;

import org.reflections.Configuration;
import org.reflections.vfs.Vfs;
import repacked.com.google.common.base.Predicate;
import repacked.com.google.common.collect.Multimap;

import javax.annotation.Nullable;

/**
 *
 */
public interface Scanner {

  void setConfiguration(Configuration configuration);

  Multimap<String, String> getStore();

  void setStore(Multimap<String, String> store);

  Scanner filterResultsBy(Predicate<String> filter);

  boolean acceptsInput(String file);

  Object scan(Vfs.File file, @Nullable Object classObject);

  boolean acceptResult(String fqn);
}
