package junit.org.rapidpm.ddi.classresolver;

import org.junit.Test;
import org.rapidpm.ddi.DI;

import javax.inject.Inject;

/**
 * Created by b.bosch on 02.12.2015.
 */
public class ClassResolverTest012 {


  @Test(expected = RuntimeException.class)
  public void test001() throws Exception {
    Service service = new Service();
    DI.activateDI(service);
  }

  public interface SubService {
  }

  public static class Service {
    @Inject SubService subService;
  }

  public static class FaultySubService implements SubService {
    public FaultySubService() {
      throw new RuntimeException("OOPS");
    }
  }
}
