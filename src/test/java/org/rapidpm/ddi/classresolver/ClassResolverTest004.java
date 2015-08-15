package org.rapidpm.ddi.classresolver;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.implresolver.ClassResolver;
import org.rapidpm.ddi.implresolver.DDIModelException;
import org.rapidpm.ddi.implresolver.ResponsibleForInterface;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by svenruppert on 05.08.15.
 */
public class ClassResolverTest004 {
  @Test(expected = DDIModelException.class)
  public void testProducer001() throws Exception {

    final BusinessModule businessModule = new BusinessModule();
    try {
      DI.activateDI(businessModule);
    } catch (DDIModelException e) {
      final String message = e.getMessage();
      Assert.assertTrue(message.contains("interface with multiple implementations and more as 1 ClassResolver"));
      throw e;
    }

    Assert.fail();
  }

  @ResponsibleForInterface(Service.class)
  public static class ServiceClassResolverA implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return null;
    }
  }

  @ResponsibleForInterface(Service.class)
  public static class ServiceClassResolverB implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return null;
    }
  }


  public static class BusinessModule {
    @Inject Service service;

    public String work(String txt) {
      return service.work(txt);
    }
  }


  public interface Service {
    String work(String txt);
  }


  public static class ServiceImplA implements Service {
    public String work(String txt) {
      return txt;
    }
  }

  public static class ServiceImplB implements Service {
    public String work(String txt) {
      return txt;
    }

  }
}
