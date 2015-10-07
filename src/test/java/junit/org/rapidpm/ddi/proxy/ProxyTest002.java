package junit.org.rapidpm.ddi.proxy;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;

import javax.inject.Inject;

/**
 * Created by svenruppert on 23.07.15.
 */
public class ProxyTest002 extends DDIBaseTest {


  @Test
  public void test001() throws Exception {
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModul businessModul = new BusinessModul();
    DI.activateDI(businessModul);

    Assert.assertNotNull(businessModul);
    Assert.assertNotNull(businessModul.service);
  }

  @Test
  public void test002() throws Exception {
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModulSecure businessModul = new BusinessModulSecure();
    DI.activateDI(businessModul);

    Assert.assertNotNull(businessModul);
    Assert.assertNotNull(businessModul.service);
  }

  public interface Service {
    String doWork(String str);
  }

  public static class BusinessModulSecure {
    @Inject @Proxy(virtual = true, metrics = true) Service service;

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class BusinessModul {
    @Inject @Proxy(secure = false) Service service;

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class ServiceA implements Service {

    public ServiceA() {
      System.out.println(" ServiceA = constructed...");
    }

    @Override
    public String doWork(final String str) {
      return "ServiceA_" + str;
    }
  }

}
