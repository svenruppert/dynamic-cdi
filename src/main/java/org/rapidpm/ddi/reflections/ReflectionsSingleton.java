package org.rapidpm.ddi.reflections;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Created by svenruppert on 14.07.15.
 */
public class ReflectionsSingleton {

  private ReflectionsSingleton() {
  }

  public static final Reflections REFLECTIONS = new Reflections(new ConfigurationBuilder()
      .setUrls(ClasspathHelper.forPackage(""))
      .setScanners(
          new SubTypesScanner(),
          new TypeAnnotationsScanner(),
          new MethodAnnotationsScanner())
  );


}

