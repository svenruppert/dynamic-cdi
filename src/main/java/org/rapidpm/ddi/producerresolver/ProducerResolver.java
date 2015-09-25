package org.rapidpm.ddi.producerresolver;

import org.rapidpm.ddi.producer.Producer;

/**
 * Created by svenruppert on 25.09.15.
 */
public interface ProducerResolver<I, P extends Producer<I>> {
  Class<? extends Producer<I>> resolve(Class<? extends I> interf);
}
