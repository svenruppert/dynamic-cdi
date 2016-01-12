package junit.org.rapidpm.ddi.reflectionmodel.v005.model002;

import junit.org.rapidpm.ddi.reflectionmodel.v005.api.Service;
import org.rapidpm.proxybuilder.staticgenerated.annotations.StaticMetricsProxy;

/**
 * Created by svenruppert on 09.01.16.
 */
@StaticMetricsProxy
public class ServiceImpl002 implements Service {
  @Override
  public String doWork(final String string) {
    return string;
  }
}
