package junit.org.rapidpm.ddi.bootstrap.test001.load;

import junit.org.rapidpm.ddi.bootstrap.test001.api.Service;

/**
 * Created by benjamin-bosch on 06.04.17.
 */
public class GoodServiceImpl implements Service{
  @Override
  public String doWork() {
    return "done";
  }
}
