package junit.org.rapidpm.ddi.reflections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.reflections.ReflectionUtils;

/**
 * ReflectionUtils Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jan 25, 2016</pre>
 */
public class ReflectionUtilsTest {

  @Before
  public void before() throws Exception {
  }

  @After
  public void after() throws Exception {
  }

  /**
   * Method: checkInterface(final Type aClass, Class targetInterface)
   */
  @Test
  public void testCheckInterface() throws Exception {

    final ReflectionUtils utils = new ReflectionUtils();

    Assert.assertTrue(utils.checkInterface(ServiceImpl_A.class, Service.class));
    Assert.assertFalse(utils.checkInterface(Service.class, ServiceImpl_A.class));

    Assert.assertFalse(utils.checkInterface(ServiceImpl_A.class, NoService.class));
    Assert.assertFalse(utils.checkInterface(NoService.class, ServiceImpl_A.class));


    Assert.assertFalse(utils.checkInterface(NoService.class, Service.class));
    Assert.assertFalse(utils.checkInterface(Service.class, NoService.class));

    Assert.assertFalse(utils.checkInterface(Service.class, ServiceLev2.class));
    Assert.assertTrue(utils.checkInterface(ServiceLev2.class, Service.class));

    Assert.assertTrue(utils.checkInterface(ServiceImpl_B.class, Service.class));
    Assert.assertFalse(utils.checkInterface(Service.class, ServiceImpl_B.class));

    Assert.assertFalse(utils.checkInterface(ServiceImpl_B.class, NoService.class));
    Assert.assertFalse(utils.checkInterface(NoService.class, ServiceImpl_B.class));

  }


  public interface Service {
  }

  public interface ServiceLev2 extends Service {
  }


  public interface NoService {
  }

  public static class ServiceImpl_A implements Service {
  }

  public static class ServiceImpl_B implements ServiceLev2 {
  }


}
