package org.rapidpm.ddi;

import org.rapidpm.ddi.producer.InstanceCreator;
import org.rapidpm.proxybuilder.type.dymamic.virtual.VirtualDynamicProxyInvocationHandler.ServiceFactory;

/**
 * Created by Sven Ruppert on 23.07.15.
 */
public class DDIServiceFactory<C> implements ServiceFactory<C> {

  private final Class<C> realClass;

  public DDIServiceFactory(final Class<C> realClass) {
    this.realClass = realClass;
  }

  @Override
  public C createInstance() {
    return new InstanceCreator().instantiate(realClass);
  }
}

