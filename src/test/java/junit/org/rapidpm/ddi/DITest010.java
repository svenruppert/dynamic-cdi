package junit.org.rapidpm.ddi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;

/**
 * Created by svenruppert on 21.12.15.
 */
public class DITest010 {


  @Test
  public void test001() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
    final Service service = DI.activateDI(Service.class);
    Assert.assertNotNull(service);
    Assert.assertEquals(ServiceImpl.class, service.getClass());
  }


  @After
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
  }

  public interface Service {
  }

  public static class ServiceImpl implements Service {
  }

}
