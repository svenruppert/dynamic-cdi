package org.rapidpm.ddi.implresolver;

import org.rapidpm.ddi.ReflectionsSingleton;

import java.util.Set;

/**
 * Created by svenruppert on 23.07.15.
 */
public class ImplementingClassResolver<I> implements ClassResolver<I> {

  public Class<I> resolve(Class<I> interf) {
    if (interf.isInterface()) {
      final Set<Class<? extends I>> subTypesOf = ReflectionsSingleton.REFLECTIONS.getSubTypesOf(interf);
      if (subTypesOf.isEmpty()) {

        //search for Producer
//        final Set<Method> result = new HashSet<>();
//        final Set<Method> methodsAnnotatedWith = ReflectionsSingleton.REFLECTIONS.getMethodsAnnotatedWith(Produces.class);
//        for (Method method : methodsAnnotatedWith) {
//          final Class<?> returnType = method.getReturnType();
//          if (returnType.equals(interf)) {
//            result.add(method);
//            if (result.size() > 1) {
//              throw new DDIModelException("to many Producer Methods for the following interface " + interf);
//            }
//          }
//        }
//
//        if (result.isEmpty()) throw new DDIModelException("could not find a producer for " + interf);




      } else if (subTypesOf.size() == 1) {
        return (Class<I>) subTypesOf.toArray()[0];
      } else {
        final Set<Class<? extends ClassResolver>> subTypesOfClassResolver = ReflectionsSingleton.REFLECTIONS.getSubTypesOf(ClassResolver.class);
        final boolean remove = subTypesOfClassResolver.remove(ImplementingClassResolver.class);
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
        } else if (subTypesOfClassResolver.size() < 1) {
          throw new DDIModelException("interface with multiple implementations and no ClassResolver= " + interf);
        }
      }
    } else {
      return interf;
    }
    throw new DDIModelException("this point should never been reached.. no decission possible for = " + interf);
  }

//  public <I> Class<I> resolveProducer(Class<I> interf) {
//    if (interf.isInterface()) {
//      final Set<Class<? extends I>> subTypesOf = ReflectionsSingleton.REFLECTIONS.getSubTypesOf(interf);
//      if (subTypesOf.isEmpty()) {
//        throw new DDIModelException("could not find an implementation for " + interf);
//      } else if (subTypesOf.size() == 1) {
//        return (Class<I>) subTypesOf.toArray()[0];
//      } else {
//        throw new DDIModelException("interface with multiple implementations= " + interf);
//      }
//    } else {
//      return interf;
//    }
//  }


}

