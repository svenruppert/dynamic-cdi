package junit.org.rapidpm.ddi.instancecreator;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.producer.InstanceCreator;

/**
 * Created by svenruppert on 12.10.15.
 */
public class InstanceCreatorTest001 {


  @Test
  public void test001() throws Exception {
    DI.activateDI("org.rapidpm");
    DI.activateDI("junit.org.rapidpm");
    final Service instantiate = new InstanceCreator().instantiate(Service.class);
    Assert.assertNotNull(instantiate);

  }


  public interface Service {

  }

  public static class ServiceImpl implements Service {

  }



}
