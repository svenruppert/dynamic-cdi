package org.rapidpm.ddi.producer;

import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.producerresolver.ProducerResolver;
import org.rapidpm.ddi.producerresolver.ProducerResolverLocator;
import org.rapidpm.ddi.scopes.InjectionScopeManager;

import java.util.Set;

/**
 * Created by Sven Ruppert on 17.08.15.
 */
public class InstanceCreator {

  public <T> T instantiate(Class<T> clazz) {

    T newInstance;
    if (clazz.isInterface()) {
      final Class<? extends T> resolve = DI.resolveImplementingClass(clazz);
      newInstance = createNewInstance(clazz, resolve);
    } else {
      newInstance = createNewInstance(clazz, clazz);
    }
    return newInstance;
  }

  private <T> T createNewInstance(final Class classOrInterf, final Class clazz) {
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

    //Check Scopes..
    final boolean managedByMeTarget = InjectionScopeManager.isManagedByMe(classOrInterf);
    final boolean managedByMeImpl = InjectionScopeManager.isManagedByMe(resolverTarget);

    if (managedByMeTarget) {
      final T cast = (T) InjectionScopeManager.getInstance(classOrInterf);
      if (cast != null) {
        return cast;
      }
    } else if (managedByMeImpl) {
      final T cast = (T) InjectionScopeManager.getInstance(resolverTarget);
      if (cast != null) {
        return cast;
      }
    }

    final Set<Class<?>> producerClassses = new ProducerLocator().findProducersFor(classOrInterf);

    if (producerClassses.size() == 1) {
      final Class cls = (Class) producerClassses.toArray()[0];
      final T result = createInstanceWithThisProducer(cls);
      putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
      return result;
    } else if (producerClassses.size() > 1) {
      final Set<Class<? extends ProducerResolver>> producerResolverClasses
          = new ProducerResolverLocator().findProducersResolverFor(resolverTarget);
      if (producerResolverClasses.size() == 1) {
        final Class<? extends ProducerResolver> producerResolverClass
            = (Class<? extends ProducerResolver>) producerResolverClasses.toArray()[0];
        try {
          final ProducerResolver producerResolver = producerResolverClass.newInstance();
          DI.activateDI(producerResolver);
          final T result = createInstanceWithThisProducer(producerResolver.resolve(resolverTarget));
          putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
          return result;
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
        final T result;
        try {
          result = (T) clazz.newInstance();
          DI.activateDI(result);
          putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
          return result;
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
      final T instance = newInstance.create();
      return DI.activateDI(instance);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      throw new DDIModelException(e);
    }
  }

  private <T> void putToScope(final Class classOrInterf, final Class clazz, final boolean managedByMeTarget, final boolean managedByMeImpl, final T result) {
    if (managedByMeTarget && managedByMeImpl) {
      InjectionScopeManager.manageInstance(classOrInterf, result);
    } else if (managedByMeTarget) {
      InjectionScopeManager.manageInstance(classOrInterf, result);
    } else if (managedByMeImpl) InjectionScopeManager.manageInstance(clazz, result);
  }


}
