package org.rapidpm.ddi.proxy;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;

import javax.inject.Inject;

/**
 * Created by svenruppert on 23.07.15.
 */
public class ProxyTest002 {



  @Test
  public void test001() throws Exception {
    final BusinessModul businessModul = new BusinessModul();
    DI.getInstance().activateDI(businessModul);

    Assert.assertNotNull(businessModul);
    Assert.assertNotNull(businessModul.service);




  }

  public static class BusinessModulSecure{
    @Inject @Proxy(secure = true) Service service;
    public String work(String str){
      return service.doWork(str);
    }
  }

  public static class BusinessModul{
    @Inject @Proxy(secure = false) Service service;
    public String work(String str){
      return service.doWork(str);
    }
  }

  public interface Service {
    String doWork(String str);
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
