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

package org.rapidpm.ddi.producerresolver;

import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;

import java.util.Iterator;
import java.util.Set;

public class ProducerResolverLocator {

  public Set<Class<? extends ProducerResolver>> findProducersResolverFor(final Class clazzOrInterf) {

    final Set<Class<? extends ProducerResolver>> producerResolverClasses = DI.getSubTypesOf(ProducerResolver.class);
    final Iterator<Class<? extends ProducerResolver>> iterator = producerResolverClasses.iterator();
    while (iterator.hasNext()) {
      final Class<? extends ProducerResolver> nextProducerResolverClass = iterator.next();
      if (nextProducerResolverClass.isAnnotationPresent(ResponsibleFor.class)) {
        final ResponsibleFor responsibleFor = nextProducerResolverClass.getAnnotation(ResponsibleFor.class);
        final Class<? extends ResponsibleFor> responsibleForClass = responsibleFor.value();
//        if (responsibleForClass == null)
//          throw new DDIModelException("ProducerResolver without ResponsibleFor " + nextProducerResolverClass.getName());
        if (!responsibleForClass.equals(clazzOrInterf)) iterator.remove();
      } else {
        throw new DDIModelException("ProducerResolver without ResponsibleFor Annotation " + nextProducerResolverClass.getName());
      }
    }
    return producerResolverClasses;
  }
}
