package org.rapidpm.ddi.producerresolver;

import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by svenruppert on 25.09.15.
 */
public class ProducerResolverLocator {

  public Set<Class<? extends ProducerResolver>> findProducersResolverFor(final Class clazzOrInterf) {

    final Set<Class<? extends ProducerResolver>> producerResolverClasses = DI.getSubTypesOf(ProducerResolver.class);
    final Iterator<Class<? extends ProducerResolver>> iterator = producerResolverClasses.iterator();
    while (iterator.hasNext()) {
      final Class<? extends ProducerResolver> nextProducerResolverClass = iterator.next();
      if (nextProducerResolverClass.isAnnotationPresent(ResponsibleFor.class)) {
        final ResponsibleFor responsibleFor = nextProducerResolverClass.getAnnotation(ResponsibleFor.class);
        final Class<? extends ResponsibleFor> responsibleForClass = responsibleFor.value();
        if (responsibleForClass == null)
          throw new DDIModelException("ProducerResolver without ResponsibleFor " + nextProducerResolverClass.getName());
        if (!responsibleForClass.equals(clazzOrInterf)) iterator.remove();
      } else {
        throw new DDIModelException("ProducerResolver without ResponsibleFor Annotation" + nextProducerResolverClass.getName());
      }
    }
    return producerResolverClasses;
  }
}
