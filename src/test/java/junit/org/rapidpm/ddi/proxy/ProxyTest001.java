package junit.org.rapidpm.ddi.proxy;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;

import javax.inject.Inject;

/**
 * Created by svenruppert on 17.07.15.
 */
public class ProxyTest001 extends DDIBaseTest {

  @Test
  public void testProxy001() throws Exception {
    final BusinessModulVirtual instance = new BusinessModulVirtual();
    Assert.assertNotNull(instance);
    Assert.assertNull(instance.service);

    DI.activateDI(instance);

    Assert.assertNotNull(instance.service);
    Assert.assertTrue(java.lang.reflect.Proxy.isProxyClass(instance.service.getClass()));

    final String hello = instance.service.doWork("Hello");
    Assert.assertNotNull(hello);
    Assert.assertEquals("ServiceA_Hello", hello);
  }

  @Test
  public void testProxy002() throws Exception {


    final BusinessModul instance = new BusinessModul();
    Assert.assertNotNull(instance);
    Assert.assertNull(instance.service);

    DI.activateDI(instance);

    Assert.assertNotNull(instance.service);
    Assert.assertFalse(java.lang.reflect.Proxy.isProxyClass(instance.service.getClass()));

    final String hello = instance.service.doWork("Hello");
    Assert.assertNotNull(hello);
    Assert.assertEquals("ServiceA_Hello", hello);


  }


  public interface Service {
    String doWork(String str);
  }

  public static class BusinessModulVirtual {
    @Inject @Proxy(virtual = true) Service service;

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class BusinessModul {
    @Inject @Proxy(virtual = false) Service service;

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
