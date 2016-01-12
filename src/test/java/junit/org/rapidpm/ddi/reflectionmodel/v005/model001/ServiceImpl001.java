package junit.org.rapidpm.ddi.reflectionmodel.v005.model001;

import junit.org.rapidpm.ddi.reflectionmodel.v005.api.Service;

/**
 * Created by svenruppert on 09.01.16.
 */
public class ServiceImpl001 implements Service {
  @Override
  public String doWork(final String string) {
    return string;
  }
}
