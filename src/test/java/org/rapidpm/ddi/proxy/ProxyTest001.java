package org.rapidpm.ddi.proxy;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by svenruppert on 17.07.15.
 */
public class ProxyTest001 {



  private final Map<String, String> infos = new HashMap<>();

  @Test
  public void testProxy001() throws Exception {
    final DI di = new DI();

    final BusinessModul instance = new BusinessModul();
    Assert.assertNotNull(instance);
    Assert.assertNull(instance.service);

    di.activateDI(instance);

    Assert.assertNotNull(instance.service);
    final String hello = instance.service.doWork("Hello");
    Assert.assertNotNull(hello);
    Assert.assertEquals("ServiceA_Hello", hello);


  }



  public static class BusinessModul{
    @Inject @Proxy() Service service;

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
