package org.rapidpm.ddi.reflections;

import org.reflections.scanners.AbstractScanner;
import org.reflections.util.FilterBuilder;


/**
 * Created by svenruppert on 09.01.16.
 */
public class PkgTypesScanner extends AbstractScanner {

  /**
   * created new SubTypesScanner. will exclude direct Object subtypes
   */
  public PkgTypesScanner() {
    this(true); //exclude direct Object subtypes by default
  }

  /**
   * created new SubTypesScanner.
   *
   * @param excludeObjectClass if false, include direct {@link Object} subtypes in results.
   */
  public PkgTypesScanner(boolean excludeObjectClass) {
    if (excludeObjectClass) {
      filterResultsBy(new FilterBuilder().exclude(Object.class.getName())); //exclude direct Object subtypes
    }
  }

  @SuppressWarnings({"unchecked"})
  public void scan(final Object cls) {
    String className = getMetadataAdapter().getClassName(cls);

    int index = className.lastIndexOf(".");
    if (index != -1) {

      final String pkgName = className.substring(0, index);
      if (acceptResult(className)) {
        getStore().put(pkgName, className);
      }
    }
  }
}
