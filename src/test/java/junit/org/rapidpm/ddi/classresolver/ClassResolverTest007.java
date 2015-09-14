package junit.org.rapidpm.ddi.classresolver;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;
import org.rapidpm.ddi.implresolver.DDIModelException;

import javax.inject.Inject;

/**
 * Created by svenruppert on 01.09.15.
 */
public class ClassResolverTest007 {


  /**
   * Anonymous Class inside the create() Method.. will be removed
   *
   * 1 Interface , 1 Impl -> Impl ServiceA will be used
   *
   * @throws Exception
   */
  @Test(expected = DDIModelException.class)
  public void testProxy001() throws Exception {
    final BusinessModulVirtual instance = new BusinessModulVirtual();
    Assert.assertNotNull(instance);
    Assert.assertNull(instance.service);
    DI.activateDI(instance);
    Assert.assertEquals("created by Anonymous", instance.work(""));
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
    private Service service = new Service() { //this is a implementation of the Interface...
      @Override
      public String doWork(final String str) {
        return "created by Anonymous";
      }
    };

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
