package junit.org.rapidpm.ddi.reflectionmodel.v006.model001;

import junit.org.rapidpm.ddi.reflectionmodel.v006.api.Service;

/**
 * Created by svenruppert on 09.01.16.
 */
public class ServiceImpl001 implements Service {
  @Override
  public String doWork(final String string) {
    return string;
  }
}
