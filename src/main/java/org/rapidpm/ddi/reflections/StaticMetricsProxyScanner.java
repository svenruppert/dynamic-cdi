package org.rapidpm.ddi.reflections;

import org.rapidpm.proxybuilder.staticgenerated.annotations.IsMetricsProxy;
import org.reflections.scanners.AbstractScanner;
import org.reflections.util.FilterBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by svenruppert on 09.01.16.
 */
public class StaticMetricsProxyScanner extends AbstractScanner {

  /**
   * created new SubTypesScanner. will exclude direct Object subtypes
   */
  public StaticMetricsProxyScanner() {
    this(true); //exclude direct Object subtypes by default
  }

  /**
   * created new SubTypesScanner.
   *
   * @param excludeObjectClass if false, include direct {@link Object} subtypes in results.
   */
  public StaticMetricsProxyScanner(boolean excludeObjectClass) {
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
    if (classAnnotationNames.contains(IsMetricsProxy.class.getName())) {
      if (superclassName.isEmpty()) {
        //
      } else {
        getStore().put(superclassName, className);
      }
      interfacesNames.forEach(c -> getStore().put(c, className));
    }
  }
}

