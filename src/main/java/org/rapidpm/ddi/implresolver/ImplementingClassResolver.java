package org.rapidpm.ddi.implresolver;

import org.rapidpm.ddi.reflections.ReflectionsSingleton;

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
      final Set<Class<? extends I>> subTypesOf = ReflectionsSingleton.REFLECTIONS.getSubTypesOf(interf);
      if (subTypesOf.isEmpty()) {
        //TODO scann for producer....
//        throw new DDIModelException("could not find a subtype of " + interf);
        return interf;
      } else if (subTypesOf.size() == 1) {
        return (Class<I>) subTypesOf.toArray()[0];
      } else {
        final Set<Class<? extends ClassResolver>> subTypesOfClassResolver = ReflectionsSingleton.REFLECTIONS.getSubTypesOf(ClassResolver.class);
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
          throw new DDIModelException("interface with multiple implementations and no ClassResolver= " + interf);
        }
      }
    } else {
      return interf;
    }
    throw new DDIModelException("this point should never been reached.. no decission possible for = " + interf);
  }

}

