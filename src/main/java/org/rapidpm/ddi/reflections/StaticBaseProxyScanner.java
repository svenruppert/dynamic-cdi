package org.rapidpm.ddi.reflections;

import org.reflections.scanners.AbstractScanner;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by RapidPM - Team on 10.05.16.
 */
public abstract class StaticBaseProxyScanner extends AbstractScanner {

  /**
   * created new SubTypesScanner. will exclude direct Object subtypes
   */
  public StaticBaseProxyScanner() {
    this(true); //exclude direct Object subtypes by default
  }

  /**
   * created new SubTypesScanner.
   *
   * @param excludeObjectClass if false, include direct {@link Object} subtypes in results.
   */
  public StaticBaseProxyScanner(boolean excludeObjectClass) {
    if (excludeObjectClass) {
      filterResultsBy(new FilterBuilder().exclude(Object.class.getName())); //exclude direct Object subtypes
    }
  }

  @SuppressWarnings({"unchecked"})
  public void scan(final Object cls) {
    String className = getMetadataAdapter().getClassName(cls);
    final List<String> interfacesNames = getMetadataAdapter().getInterfacesNames(cls);
    final String superclassName = getMetadataAdapter().getSuperclassName(cls);

    final Set<String> classAnnotationNames = new HashSet<>(getMetadataAdapter().getClassAnnotationNames(cls));
    if (classAnnotationNames.contains(responsibleForAnnotation().getName())) {
      if (!superclassName.isEmpty()) {
        getStore().put(superclassName, className);
      }
      interfacesNames.forEach(c -> getStore().put(c, className));
    }
  }

  protected abstract Class<? extends Annotation> responsibleForAnnotation();
}