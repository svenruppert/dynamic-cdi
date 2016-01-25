package org.rapidpm.ddi.scopes;

/**
 * Created by Sven Ruppert on 20.01.16.
 */
public abstract class InjectionScope {

  public abstract <T> T getInstance(String clazz);


  public abstract <T> void storeInstance(final Class<T> targetClassOrInterface, final T instance);

  public abstract void clear();


  public abstract String getScopeName();

}
