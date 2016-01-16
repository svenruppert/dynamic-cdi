package org.rapidpm.ddi.implresolver;

/**
 * Created by Sven Ruppert on 31.07.15.
 */
public interface ClassResolver<T> {
  Class<? extends T> resolve(Class<T> interf);
}
