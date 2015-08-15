package org.rapidpm.ddi.classresolver;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.implresolver.ClassResolver;
import org.rapidpm.ddi.implresolver.DDIModelException;
import org.rapidpm.ddi.implresolver.ResponsibleForInterface;

import javax.inject.Inject;

/**
 * Created by svenruppert on 05.08.15.
 */
public class ClassResolverTest005 {

  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    try {
      DI.checkActiveModel();
    } catch (DDIModelException e) {
      final String message = e.getMessage();
      Assert.assertTrue(message.contains("Found ClassResolver without @ResponsibleForInterface annotation"));
      throw e;
    }
    Assert.fail();
  }

  @Test
  public void test002() throws Exception {
    final BusinessModule businessModule = new BusinessModule();
    DI.activateDI(businessModule);

    Assert.assertEquals(ServiceImplA.class, businessModule.service.getClass());

  }

  @ResponsibleForInterface(Service.class)
  public static class ServiceClassResolverA implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return ServiceImplA.class;
    }
  }

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
