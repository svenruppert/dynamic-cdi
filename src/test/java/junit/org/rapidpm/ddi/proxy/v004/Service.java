package junit.org.rapidpm.ddi.proxy.v004;

import org.rapidpm.proxybuilder.dynamicobjectadapter.DynamicObjectAdapterBuilder;

/**
 * Created by svenruppert on 17.08.15.
 */
@DynamicObjectAdapterBuilder
public interface Service {
  String doWork(String txt);
}
