package junit.org.rapidpm.ddi.scopes.v004;

import org.rapidpm.proxybuilder.objectadapter.annotations.dynamicobjectadapter.DynamicObjectAdapterBuilder;

@DynamicObjectAdapterBuilder
public interface Service {
  long value();
}
