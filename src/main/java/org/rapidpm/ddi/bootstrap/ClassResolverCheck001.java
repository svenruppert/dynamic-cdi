package org.rapidpm.ddi.bootstrap;

import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;
import org.rapidpm.ddi.implresolver.ImplementingClassResolver;

import java.util.Set;

/**
 * Created by Sven Ruppert on 06.08.15.
 */
public class ClassResolverCheck001 {


  public void execute() {
    final Set<Class<? extends ClassResolver>> subTypesOfClassResolver = DI.getSubTypesOf(ClassResolver.class);
    final boolean remove = subTypesOfClassResolver.remove(ImplementingClassResolver.class);

//        ClassResolver responsible for interface
    for (final Class<? extends ClassResolver> aClassResolver : subTypesOfClassResolver) {
      if (aClassResolver.isAnnotationPresent(ResponsibleFor.class)) {
        //ok
      } else {
        throw new DDIModelException("Found ClassResolver without @ResponsibleFor annotation= " + aClassResolver);
      }
    }
  }


}
