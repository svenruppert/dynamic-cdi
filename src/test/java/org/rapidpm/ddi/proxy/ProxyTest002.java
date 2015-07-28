package org.rapidpm.ddi.proxy;

import org.rapidpm.ddi.Proxy;

import javax.inject.Inject;

/**
 * Created by svenruppert on 23.07.15.
 */
public class ProxyTest002 {

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
