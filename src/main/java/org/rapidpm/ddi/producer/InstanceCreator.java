package org.rapidpm.ddi.producer;

import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.implresolver.DDIModelException;
import org.rapidpm.ddi.implresolver.ImplementingClassResolver;

import java.util.Set;

/**
 * Created by svenruppert on 17.08.15.
 */
public class InstanceCreator {

  public  <T> T instantiate(Class<T> clazz) {
    //check scope -> Singleton
    //check scope -> ???

    T newInstance;
    if (clazz.isInterface()) {
      final Class<T> resolve = new ImplementingClassResolver().resolve(clazz);
      newInstance = createNewInstance(clazz, resolve);
    } else {
      newInstance = createNewInstance(clazz, clazz);
    }
    return newInstance;
  }

  private <T> T createNewInstance(final Class interf, final Class clazz) {
    //kann ein Interface sein, oder eine Klasse von einem ClassResolver
    final Set<Class<?>> typesAnnotatedWith = new ProducerLocator().findProducersForInterface(interf);
//    if (interf.isInterface() && clazz.isInterface()) throw new DDIModelException("no producer found for the interface " + clazz);

    if (typesAnnotatedWith.size() == 1) {
      final Class cls = (Class) typesAnnotatedWith.toArray()[0];
      try {
        Producer<T> newInstance = (Producer<T>) cls.newInstance();
        DI.activateDI(newInstance);
        return newInstance.create();
      } catch (InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
        throw new DDIModelException(e);
      }
    } else if (typesAnnotatedWith.size() > 1) {
      throw new DDIModelException(" to many producer methods found for " + interf + " - " + typesAnnotatedWith);
    } else if (typesAnnotatedWith.isEmpty()) {

      if (clazz.isInterface()) {
        throw new DDIModelException(" only interfaces found for " + interf);
      } else {
        final T newInstance;
        try {
          newInstance = (T) clazz.newInstance();
          return newInstance;
        } catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
          throw new DDIModelException(e);
        }
      }
    }
    throw new RuntimeException("this point should never reached...");
  }

}
