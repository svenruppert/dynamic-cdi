package org.rapidpm.ddi.implresolver;

/**
 * Created by svenruppert on 31.07.15.
 */
public interface ClassResolver<I> {
  Class<? extends I> resolve(Class<I> interf);
}
