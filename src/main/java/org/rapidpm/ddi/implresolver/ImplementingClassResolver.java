package org.rapidpm.ddi.implresolver;

import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.producer.ProducerLocator;
import org.rapidpm.proxybuilder.objectadapter.annotations.staticobjectadapter.IsStaticObjectAdapter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * one subtype - will return this class
 * n subtypes - will search for classresolver to decide what will be the right implementation
 * <p>
 * no subtype - will return the interface itself, maybe on the fly implementations are available
 * <p>
 * Created by svenruppert on 23.07.15.
 */
//public class ImplementingClassResolver<I> implements ClassResolver<I> {
public class ImplementingClassResolver {

  private Map<Class, Class> resolverCache = new ConcurrentHashMap<>();

  public void clearCache() {
    resolverCache.clear();
  }


  public synchronized <I> Class<? extends I> resolve(Class<I> interf) {
    if (!resolverCache.containsKey(interf)) {
      resolverCache.put(interf, resolveNewForClass(interf));
    }
    return resolverCache.get(interf);
  }

  private <I> Class<? extends I> resolveNewForClass(Class<I> interf) {
    if (interf.isInterface()) {
      final Set<Class<? extends I>> subTypesOf = DI.getSubTypesOf(interf);

      //remove subtypes that are interfaces
      final Iterator<Class<? extends I>> iteratorOfSubTypes = subTypesOf.iterator();
      while (iteratorOfSubTypes.hasNext()) {
        Class<? extends I> next = iteratorOfSubTypes.next();
        if (next.isInterface()) iteratorOfSubTypes.remove();
        //remove Adapters -  http://rapidpm.myjetbrains.com/youtrack/issue/DDI-5
        //DDI-5
        if (next.isAnnotationPresent(IsStaticObjectAdapter.class)) iteratorOfSubTypes.remove();

      }
      if (subTypesOf.isEmpty()) return interf;
      else if (subTypesOf.size() == 1) {
        final Class<I> implClass = (Class<I>) subTypesOf.toArray()[0];
        final Set<Class<?>> producersForInterface = new ProducerLocator().findProducersFor(interf);
        final Set<Class<?>> producersForImpl = new ProducerLocator().findProducersFor(implClass);
        //@formatter:off
        if (!producersForInterface.isEmpty() && !producersForImpl.isEmpty()) return interf;
        if (producersForInterface.isEmpty()  && producersForImpl.isEmpty())  return implClass;
        if (producersForImpl.isEmpty())                                      return interf;
        if (producersForInterface.isEmpty())                                 return implClass;
        //@formatter:on
      } else {
        final Set<Class<? extends ClassResolver>> subTypesOfClassResolver = DI.getSubTypesOf(ClassResolver.class);
        // final boolean remove = subTypesOfClassResolver.remove(ImplementingClassResolver.class);

        final List<Class> clearedListOfResolvers = subTypesOfClassResolver
            .stream()
            .filter(aClassResolver -> aClassResolver.isAnnotationPresent(ResponsibleFor.class))
            .filter(aClassResolver -> {
              final ResponsibleFor responsibleFor = aClassResolver.getAnnotation(ResponsibleFor.class);
              return interf.equals(responsibleFor.value());
            })
            .collect(Collectors.toList());

        if (clearedListOfResolvers.size() == 1) {
          for (Class<? extends ClassResolver> resolver : clearedListOfResolvers) {
            try {
              final ClassResolver<I> classResolver = resolver.newInstance();
              final Class<? extends I> resolve = classResolver.resolve(interf);
              return resolve;
            } catch (InstantiationException | IllegalAccessException e) {
              e.printStackTrace();
              throw new DDIModelException(interf + " -- " + e);
            }
          }
        } else if (clearedListOfResolvers.size() > 1) {
          throw new DDIModelException("interface with multiple implementations and more as 1 ClassResolver = "
              + interf
              + " ClassResolver: " + clearedListOfResolvers);
        } else if (clearedListOfResolvers.isEmpty()) {
          final Set<Class<?>> producersForInterface = new ProducerLocator().findProducersFor(interf);
          if (producersForInterface.isEmpty()) {
            final StringBuilder stringBuilder = new StringBuilder("interface with multiple implementations and no ClassResolver= " + interf);
            final List<String> implList = subTypesOf
                .stream()
                .map(c -> "impl. : " + c.getName()).collect(Collectors.toList());
            stringBuilder.append(implList);

            throw new DDIModelException(stringBuilder.toString());
          }
          if (producersForInterface.size() == 1) return interf;

          final StringBuilder stringBuilder = new StringBuilder("interface with multiple implementations and no ClassResolver and n Producers f the interface = " + interf);

          final List<String> implList = subTypesOf.stream().map(c -> "impl. : " + c.getName()).collect(Collectors.toList());
          final List<String> prodList = producersForInterface.stream().map(c -> "producer. : " + c.getName()).collect(Collectors.toList());

          stringBuilder.append(implList).append(prodList);

          throw new DDIModelException(stringBuilder.toString());
        }
      }
    } else {
      return interf;
    }
    throw new DDIModelException("this point should never been reached.. no decission possible for = " + interf);
  }

}

