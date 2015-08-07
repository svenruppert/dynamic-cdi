package org.rapidpm.ddi.producer;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.implresolver.DDIModelException;

import javax.inject.Inject;
import javax.inject.Produces;

/**
 * Created by svenruppert on 07.08.15.
 */
public class ProducerTest002 {

  @Test(expected = DDIModelException.class)
  public void testProducer001() throws Exception {
    final BusinessModul businessModul = new BusinessModul();
    try {
      DI.getInstance().activateDI(businessModul);
    } catch (Exception e) {
      final String msg = e.getMessage();
      Assert.assertTrue(msg.contains("to many producer methods found for interface"));
       throw e;
    }

  }


  @Produces(Service.class)
  public static class ServiceProducer01 implements Producer<Service> {
    public Service create(){
      return txt -> txt + "_" + ServiceProducer01.class.getSimpleName();
    }
  }

  @Produces(Service.class)
  public static class ServiceProducer02 implements Producer<Service> {
    public Service create(){
      return txt -> txt + "_" + ServiceProducer01.class.getSimpleName();
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

}
