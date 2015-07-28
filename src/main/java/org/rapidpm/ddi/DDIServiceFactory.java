package org.rapidpm.ddi;

import org.rapidpm.proxybuilder.type.virtual.dynamic.VirtualDynamicProxyInvocationHandler;

/**
 * Created by svenruppert on 23.07.15.
 */
public class DDIServiceFactory<C> implements VirtualDynamicProxyInvocationHandler.ServiceFactory<C> {

  private Class<C> realClass;

  public DDIServiceFactory(final Class<C> realClass) {
    this.realClass = realClass;
  }

  @Override
  public C createInstance() {
    C newInstance = null;
    try {

      //TODO warum nicht hhier erst entscheiden welche impl es werden soll. ?
      newInstance = realClass.newInstance();
      //activate DDI
      DI.getInstance().activateDI(newInstance);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return newInstance;
  }
}

