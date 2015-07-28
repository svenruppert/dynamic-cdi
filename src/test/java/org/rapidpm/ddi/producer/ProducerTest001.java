package org.rapidpm.ddi.producer;

import org.junit.Test;
import org.rapidpm.ddi.DI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by svenruppert on 27.07.15.
 */
public class ProducerTest001 {


  @Test
  public void testProducer001() throws Exception {

    final BusinessModule businessModule = new BusinessModule();
    DI.getInstance().activateDI(businessModule);







  }







  public static class BusinessModule {
    @Inject Service service;
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
