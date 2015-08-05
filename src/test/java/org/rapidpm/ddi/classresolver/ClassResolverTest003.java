package org.rapidpm.ddi.classresolver;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.implresolver.ClassResolver;
import org.rapidpm.ddi.implresolver.ResponsibleForInterface;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by svenruppert on 31.07.15.
 */
public class ClassResolverTest003 {


  @Test
  public void testProducer001() throws Exception {

    final BusinessModule businessModule = new BusinessModule();
    DI.getInstance().activateDI(businessModule);
    Assert.assertNotNull(businessModule);
    Assert.assertNotNull(businessModule.service);
    Assert.assertEquals(ServiceImplB.class, businessModule.service.getClass());
  }

  @ResponsibleForInterface(Service.class)
  public static class ServiceClassResolverA implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return ServiceImplA.class;
    }
  }

  @ResponsibleForInterface(Service.class)
  public static class ServiceClassResolverB implements ClassResolver<Service> {
    @Override
    public Class<? extends Service> resolve(final Class<Service> interf) {
      return ServiceImplB.class;
    }
  }


  public static class BusinessModule {
    @Inject ServiceImplB service; //Here the concrete Class

    public String work(String txt) {
      return service.work(txt);
    }
  }


  public interface Service {
    String work(String txt);
    SubService getSubService();
    boolean isPostconstructed();
  }

  public static class ServiceImplA implements Service {
    @Inject SubService subService;

    public String work(String txt) {
      return subService.work(txt);
    }

    @Override
    public SubService getSubService() {
      return subService;
    }

    boolean postconstructed = false;

    public boolean isPostconstructed() {
      return postconstructed;
    }

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }
  }

  public static class ServiceImplB implements Service {
    @Inject SubService subService;

    public String work(String txt) {
      return subService.work(txt);
    }

    @Override
    public SubService getSubService() {
      return subService;
    }

    boolean postconstructed = false;

    public boolean isPostconstructed() {
      return postconstructed;
    }

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }
  }

  public static class SubService {
    @Inject SubSubService subSubService;

    public String work(final String txt) {
      return subSubService.work(txt);
    }

    boolean postconstructed = false;

    public boolean isPostconstructed() {
      return postconstructed;
    }

    @PostConstruct
    public void postconstruct() {
      postconstructed = true;
    }
  }

  public static class SubSubService {
    public String work(final String txt) {
      return "SubSubService " + txt;
    }
  }


}
