package junit.org.rapidpm.ddi.bootstrap.test001.not;

import junit.org.rapidpm.ddi.bootstrap.test001.api.Service;

/**
 * Created by benjamin-bosch on 06.04.17.
 */
public class BadService implements Service {
  @Override
  public String doWork() {
    throw new RuntimeException("nope");
  }
}
