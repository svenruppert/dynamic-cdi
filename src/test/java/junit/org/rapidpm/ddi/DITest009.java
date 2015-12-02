package junit.org.rapidpm.ddi;

import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;

import javax.inject.Inject;

/**
 * Created by b.bosch on 02.12.2015.
 */
public class DITest009 {

  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    Service service = new Service();
    DI.activateDI(service);

  }


  public class Service {
    @Inject SubService suService;
  }
  public interface SubService {}

  public class FaultyServiceImpl implements SubService{
    public FaultyServiceImpl() {
      throw new RuntimeException("OOOPS");
    }
  }
}
