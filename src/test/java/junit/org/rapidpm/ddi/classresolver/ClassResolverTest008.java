package junit.org.rapidpm.ddi.classresolver;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;
import javax.inject.Produces;

/**
 * Created by svenruppert on 01.09.15.
 */
public class ClassResolverTest008 {

  /**
   * no Anonymous Class inside the create() Method.. compare to ClassResolverTest007
   *
   * @throws Exception
   */
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
    Assert.assertEquals("created by Producer", hello);


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

  public static class ServiceA implements Service {

    public ServiceA() {
      System.out.println(" ServiceA = constructed...");
    }

    @Override
    public String doWork(final String str) {
      return "ServiceA_" + str;
    }
  }


  @Produces(Service.class)
  public static class ServiceProducer implements Producer<Service> {
    @Override
    public Service create() {
      return str -> "created by Producer";
    }
  }
}
