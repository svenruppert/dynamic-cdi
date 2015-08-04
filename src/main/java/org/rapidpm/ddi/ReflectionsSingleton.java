package org.rapidpm.ddi;

import org.reflections.Reflections;

/**
 * Created by svenruppert on 14.07.15.
 */
public class ReflectionsSingleton {

  private ReflectionsSingleton() {
  }

  public static final Reflections REFLECTIONS = new Reflections("");


}

