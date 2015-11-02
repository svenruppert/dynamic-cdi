package org.rapidpm.ddi.implresolver;

/**
 * Created by svenruppert on 31.07.15.
 */
public interface ClassResolver<T> {
  Class<? extends T> resolve(Class<T> interf);
}
