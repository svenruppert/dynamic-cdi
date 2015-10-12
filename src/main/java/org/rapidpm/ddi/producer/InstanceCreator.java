package org.rapidpm.ddi.producer;

import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.implresolver.ImplementingClassResolver;
import org.rapidpm.ddi.producerresolver.ProducerResolver;
import org.rapidpm.ddi.producerresolver.ProducerResolverLocator;

import java.util.Set;

/**
 * Created by svenruppert on 17.08.15.
 */
public class InstanceCreator {

  public <T> T instantiate(Class<T> clazz) {
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

  private <T> T createNewInstance(final Class classOrInterf, final Class clazz) {
    //kann ein Interface sein, oder eine Klasse von einem ClassResolver

    final Class resolverTarget;
    //explicite all combinations
    if (classOrInterf.isInterface() && !clazz.isInterface()) {
      resolverTarget = clazz;
    } else if (!classOrInterf.isInterface() && clazz.isInterface()) {
      resolverTarget = classOrInterf;
    } else if (!classOrInterf.isInterface() && !clazz.isInterface()) {
      resolverTarget = classOrInterf;
    } else { // classOrInterf.isInterface() &&  clazz.isInterface()
      resolverTarget = classOrInterf;
    }
    final Set<Class<?>> producerClassses = new ProducerLocator().findProducersFor(classOrInterf);

    if (producerClassses.size() == 1) {
      final Class cls = (Class) producerClassses.toArray()[0];
      return createInstanceWithThisProducer(cls);
    } else if (producerClassses.size() > 1) {
      final Set<Class<? extends ProducerResolver>> producerResolverClasses
          = new ProducerResolverLocator().findProducersResolverFor(resolverTarget);
      if (producerResolverClasses.size() == 1) {
        final Class<? extends ProducerResolver> producerResolverClass
            = (Class<? extends ProducerResolver>) producerResolverClasses.toArray()[0];
        try {
          final ProducerResolver producerResolver = producerResolverClass.newInstance();
          DI.activateDI(producerResolver);
          return createInstanceWithThisProducer(producerResolver.resolve(resolverTarget));
        } catch (InstantiationException | IllegalAccessException e) {
          throw new DDIModelException(e);
        }
      } else if (producerResolverClasses.size() > 1) {
        throw new DDIModelException("toooo many ProducerResolver for interface/class " + resolverTarget + " - " + producerResolverClasses);
      } else { // empty
        throw new DDIModelException(" to many Producer and no ProducerResolver found for " + classOrInterf + " - " + producerClassses);
      }
    } else if (producerClassses.isEmpty()) {

      if (clazz.isInterface()) {
        throw new DDIModelException(" only interfaces found for " + classOrInterf);
      } else {
        final T newInstance;
        try {
          newInstance = (T) clazz.newInstance();
          DI.activateDI(newInstance);
          return newInstance;
        } catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
          throw new DDIModelException(e);
        }
      }
    }
    throw new RuntimeException("this point should never reached...");
  }

  private <T> T createInstanceWithThisProducer(final Class cls) {
    try {
      Producer<T> newInstance = (Producer<T>) cls.newInstance();
      DI.activateDI(newInstance);
      return DI.activateDI(newInstance.create());
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      throw new DDIModelException(e);
    }
  }


}
