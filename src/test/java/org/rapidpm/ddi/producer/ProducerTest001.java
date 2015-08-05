package org.rapidpm.ddi.producer;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;

import javax.inject.Inject;
import javax.inject.Produces;

/**
 * Created by svenruppert on 31.07.15.
 */
public class ProducerTest001 {


  @Test
  //keine Producer unterstuetzen !!
  public void testProducer001() throws Exception {
    final BusinessModul businessModul = new BusinessModul();
    DI.getInstance().activateDI(businessModul);

    Assert.assertNotNull(businessModul);
    Assert.assertNotNull(businessModul.service);
    Assert.assertNotNull(businessModul.service.workOn("AEAE"));
    Assert.assertEquals("AEAE_"+ServiceProducer.class.getSimpleName(), businessModul.service.workOn("AEAE"));

  }


  @Produces(Service.class)
  public static class ServiceProducer implements Producer<Service> {
    public Service create(){
      return txt -> txt + "_" + ServiceProducer.class.getSimpleName();
    }
  }


  public static class BusinessModul{
    @Inject Service service;

    public String doIt(String txt){
      return service.workOn(txt);
    }
  }


  public interface Service {
    String workOn(String txt);
  }

//  public static class ServiceImplA implements Service {
//    @Override
//    public String workOn(final String txt) {
//      return txt + "_" + this.getClass().getSimpleName();
//    }
//  }
//
//  public static class ServiceImplB implements Service {
//    @Override
//    public String workOn(final String txt) {
//      return txt + "_" + this.getClass().getSimpleName();
//    }
//  }



}
