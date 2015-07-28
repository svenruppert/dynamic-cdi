package org.rapidpm.ddi.implresolver;

import org.rapidpm.ddi.ReflectionsSingleton;

import java.util.Set;

/**
 * Created by svenruppert on 23.07.15.
 */
public class ImplementingClassResolver {

  public <I> Class<I> resolve(Class<I> interf) {
    if (interf.isInterface()) {
      final Set<Class<? extends I>> subTypesOf = ReflectionsSingleton.REFLECTIONS.getSubTypesOf(interf);
      if (subTypesOf.isEmpty()) {
        throw new DDIModelException("could not find an implementation for " + interf);
      } else if (subTypesOf.size() == 1) {
        return (Class<I>) subTypesOf.toArray()[0];
      } else {
        throw new DDIModelException("interface with multiple implementations= " + interf);
        //TODO entscheidung suchen..
      }
    } else {
      return interf;
    }
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
