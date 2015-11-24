package org.rapidpm.ddi;

import org.rapidpm.ddi.producer.InstanceCreator;
import org.rapidpm.proxybuilder.type.dymamic.virtual.VirtualDynamicProxyInvocationHandler;

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
    final C instantiate = new InstanceCreator().instantiate(realClass);
    return instantiate;
  }
}

