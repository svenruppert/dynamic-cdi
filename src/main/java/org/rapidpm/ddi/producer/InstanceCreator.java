/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.rapidpm.ddi.producer;

import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.producerresolver.ProducerResolver;
import org.rapidpm.ddi.producerresolver.ProducerResolverLocator;
import org.rapidpm.ddi.scopes.InjectionScopeManager;

import java.util.Set;

import static org.rapidpm.ddi.producer.ProducerLocator.findProducersFor;

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

    final Set<Class<?>> producerClassses = findProducersFor(classOrInterf);

    if (producerClassses.size() == 1) {
      final Class cls = (Class) producerClassses.toArray()[0];
      final T result = createInstanceWithThisProducer(cls);
      putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
      return result;
    } else if (producerClassses.size() > 1) {
      return createInstanceWithProducers(classOrInterf, clazz, resolverTarget, managedByMeTarget, managedByMeImpl, producerClassses);
    } else if (producerClassses.isEmpty()) {

      if (clazz.isInterface()) {
        throw new DDIModelException(" only interfaces found for " + classOrInterf);
      } else {
//        final Set<Class<?>> producersForImpl = new ProducerLocator().findProducersFor(clazz);
//        return createInstanceWithProducers(classOrInterf, clazz, resolverTarget, managedByMeTarget, managedByMeImpl, producersForImpl);

        final T result;
        try {

          //find Producer for Impl
          final Set<Class<?>> producersForImpl = findProducersFor(clazz);
          if (producersForImpl.isEmpty()) {
            result = (T) clazz.newInstance();
//            DI.activateDI(result);
//            putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
          } else if (producersForImpl.size() > 1) {
            //TODO find ProducerResolver
            final Set<Class<? extends ProducerResolver>> producerResolverClasses
                = new ProducerResolverLocator().findProducersResolverFor(resolverTarget);

            if (producerResolverClasses.size() > 1) {
              throw new DDIModelException("to many producersResolver for Impl " + clazz + " - > " + producerResolverClasses);
            } else if (producerResolverClasses.isEmpty()) {
              throw new DDIModelException("no producersResolver for Impl " + clazz + " and n Producers - > " + producersForImpl);
            } else {
              Class<? extends ProducerResolver> producerResolverClass = (Class<? extends ProducerResolver>) producerResolverClasses.toArray()[0];
              final ProducerResolver producerResolver = producerResolverClass.newInstance();
              final Class<Producer<T>> producerClass = producerResolver.resolve(clazz);
              final Producer<T> tProducer = producerClass.newInstance();
              DI.activateDI(tProducer);
              result = tProducer.create();
//              DI.activateDI(result);
            }
//            throw new DDIModelException("to many producers for Impl " + clazz + " - > " + producersForImpl);
          } else {
            final Class<Producer<T>> producerClass = (Class<Producer<T>>) producersForImpl.toArray()[0];
            final Producer<T> tProducer = producerClass.newInstance();
            DI.activateDI(tProducer);
            result = tProducer.create();
//            DI.activateDI(result);
          }
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
      Producer<T> producer = (Producer<T>) cls.newInstance();
      DI.activateDI(producer);
      final T instance = producer.create();
//      return DI.activateDI(instance);
      return instance;
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

  private <T> T createInstanceWithProducers(final Class classOrInterf, final Class clazz, final Class resolverTarget, final boolean managedByMeTarget, final boolean managedByMeImpl, final Set<Class<?>> producerClassses) {
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
  }


}
