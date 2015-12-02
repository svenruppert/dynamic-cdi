package junit.org.rapidpm.ddi;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by b.bosch on 02.12.2015.
 */
public class DITest007 extends DDIBaseTest {

  public static final String TEST_STRING = "inner";

  @Test
  public void test001() throws Exception {
    Service service = new Service();
    Assert.assertEquals(null, service.test);
    DI.activateDI(service);
    Assert.assertEquals(TEST_STRING, service.test);
  }

  public static class Service {
    @Inject
    SubService subService;
    public String test;

    @PostConstruct
    public void construct() {
      test = subService.getName();
    }
  }

  public static class SubService {
    public String getName() {
      return TEST_STRING;
    }
  }
}
