package org.rapidpm.ddi.producer;

/**
 * Created by svenruppert on 06.08.15.
 */
public interface Producer<T> {

  T create();

}
