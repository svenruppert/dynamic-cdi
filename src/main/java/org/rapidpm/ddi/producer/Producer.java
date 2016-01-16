package org.rapidpm.ddi.producer;

/**
 * Created by Sven Ruppert on 06.08.15.
 */
public interface Producer<T> {

  T create();

}
