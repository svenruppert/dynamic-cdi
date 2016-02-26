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

package org.rapidpm.ddi.producer;

import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProducerLocator {

  //class 2 producer-set
  private static final Map<Class, Set<Class<?>>> RESOLVER_CACHE_FOR_CLASS_2_PRODUCER_SET = new ConcurrentHashMap<>();

  private ProducerLocator() {
  }

  public static void clearCache() {
    RESOLVER_CACHE_FOR_CLASS_2_PRODUCER_SET.clear();
  }

  public static Set<Class<?>> findProducersFor(final Class clazzOrInterf) {
    if (RESOLVER_CACHE_FOR_CLASS_2_PRODUCER_SET.containsKey(clazzOrInterf)) return RESOLVER_CACHE_FOR_CLASS_2_PRODUCER_SET.get(clazzOrInterf);

    final Set<Class<?>> typesAnnotatedWith = DI.getTypesAnnotatedWith(Produces.class);

    final Iterator<Class<?>> iterator = typesAnnotatedWith.iterator();
    while (iterator.hasNext()) {
      final Class producerClass = iterator.next();
      final Produces annotation = (Produces) producerClass.getAnnotation(Produces.class);
      final Class value = annotation.value();
      if (value.equals(clazzOrInterf)) {
        //TODO logger
      } else {
        iterator.remove();
      }
    }
    RESOLVER_CACHE_FOR_CLASS_2_PRODUCER_SET.put(clazzOrInterf, typesAnnotatedWith);
    return typesAnnotatedWith;
  }
}
