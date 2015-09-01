package org.rapidpm.ddi.implresolver;

import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.producer.ProducerLocator;

import java.util.Iterator;
import java.util.Set;

/**
 * one subtype - will return this class
 * n subtypes - will search for classresolver to decide what will be the right implementation
 * <p>
 * no subtype - will return the interface itself, maybe on the fly implementations are available
 * <p>
 * Created by svenruppert on 23.07.15.
 */
public class ImplementingClassResolver<I> implements ClassResolver<I> {

  public Class<I> resolve(Class<I> interf) {
    if (interf.isInterface()) {
      final Set<Class<? extends I>> subTypesOf = DI.getSubTypesOf(interf);


      if (subTypesOf.isEmpty()) {
        //TODO scann for producer....
//        throw new DDIModelException("could not find a subtype of " + interf);
        return interf;
      } else if (subTypesOf.size() == 1) {
        //check if Producer is available
        //if producer for Interface available  use this one
        //
        final Class<I> implClass = (Class<I>) subTypesOf.toArray()[0];

        final Set<Class<?>> producersForInterface = new ProducerLocator().findProducersForInterface(interf);
        final Set<Class<?>> producersForImpl = new ProducerLocator().findProducersForInterface(implClass);
        if ( ! producersForInterface.isEmpty() && ! producersForImpl.isEmpty())
          throw new DDIModelException("interface and impl. with Producer => interface = " + interf + " impl.  = " + implClass);

        if ( producersForInterface.isEmpty() && producersForImpl.isEmpty())
          return implClass;

        if (producersForImpl.isEmpty()) return interf;
        if (producersForInterface.isEmpty()) return implClass;

      } else {
        final Set<Class<? extends ClassResolver>> subTypesOfClassResolver = DI.getSubTypesOf(ClassResolver.class);
        final boolean remove = subTypesOfClassResolver.remove(ImplementingClassResolver.class);

//        ClassResolver responsible for interface
        final Iterator<Class<? extends ClassResolver>> iterator = subTypesOfClassResolver.iterator();
        while (iterator.hasNext()) {
          Class<? extends ClassResolver> aClassResolver = iterator.next();

          if (aClassResolver.isAnnotationPresent(ResponsibleForInterface.class)) {
            final ResponsibleForInterface responsibleForInterface = aClassResolver.getAnnotation(ResponsibleForInterface.class);
            final Class value = responsibleForInterface.value();
            if (interf.equals(value)) {
              //ok
            } else {
              iterator.remove();
            }
          } else {
            //TODO logger -> throw new DDIModelException("Found ClassResolver without @ResponsibleForInterface annotation= " + aClassResolver);
            iterator.remove();
          }
        }

        if (subTypesOfClassResolver.size() == 1) {
          for (Class<? extends ClassResolver> resolver : subTypesOfClassResolver) {
            try {
              final ClassResolver classResolver = resolver.newInstance();
              final Class<I> resolve = classResolver.resolve(interf);
              return resolve;
            } catch (InstantiationException | IllegalAccessException e) {
              e.printStackTrace();
              throw new DDIModelException(interf + " -- " + e);
            }
          }
        } else if (subTypesOfClassResolver.size() > 1) {
          throw new DDIModelException("interface with multiple implementations and more as 1 ClassResolver= " + interf);
        } else if (subTypesOfClassResolver.isEmpty()) {
          //TODO check if Producer for Interface available
          //yes -> return interface
          final Set<Class<?>> producersForInterface = new ProducerLocator().findProducersForInterface(interf);
          if(producersForInterface.isEmpty()) throw new DDIModelException("interface with multiple implementations and no ClassResolver= " + interf);
          if(producersForInterface.size() == 1) return interf;
          throw new DDIModelException("interface with multiple implementations and no ClassResolver and n Producers f the interface = " + interf);
        }
      }
    } else {
      return interf;
    }
    throw new DDIModelException("this point should never been reached.. no decission possible for = " + interf);
  }

}

