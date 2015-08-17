package org.rapidpm.ddi.producer;

import org.jetbrains.annotations.NotNull;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.implresolver.DDIModelException;

import javax.inject.Produces;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by svenruppert on 17.08.15.
 */
public class ProducerLocator {

  @NotNull
  public Set<Class<?>> findProducersForInterface(final Class interf) {
    final Set<Class<?>> typesAnnotatedWith = DI.getTypesAnnotatedWith(Produces.class);

    final Iterator<Class<?>> iterator = typesAnnotatedWith.iterator();
    while (iterator.hasNext()) {
      Class producerClass = iterator.next();
      final Produces annotation = (Produces) producerClass.getAnnotation(Produces.class);
      final Class value = annotation.value();
      if (value == null) throw new DDIModelException("Producer without target Interface " + producerClass);
      if (value.equals(interf)) {
        //TODO logger
      } else {
        iterator.remove();
      }
    }
    return typesAnnotatedWith;
  }
}
