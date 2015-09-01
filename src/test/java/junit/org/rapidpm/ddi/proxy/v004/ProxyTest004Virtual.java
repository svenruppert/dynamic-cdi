package junit.org.rapidpm.ddi.proxy.v004;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;
import org.rapidpm.ddi.implresolver.DDIModelException;
import org.rapidpm.proxybuilder.type.virtual.dynamic.ServiceStrategyFactoryNotThreadSafe;
import org.rapidpm.proxybuilder.type.virtual.dynamic.VirtualDynamicProxyInvocationHandler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.Optional;

/**
 * Created by svenruppert on 17.08.15.
 */
public class ProxyTest004Virtual {

  public static class BusinessModule001 {
    @Inject Service service;

    boolean post = false;

    @PostConstruct
    public void postconstruct() {
      post = true;
    }
  }


  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
//    DI.activatePackages("junit.org.rapidpm"); //expliziet deactivierte pkgs
    try {
      final BusinessModule001 businessModule001 = DI.activateDI(new BusinessModule001());
    } catch (Exception e) {
      if (e instanceof DDIModelException) {
        final String message = e.getMessage();
        Assert.assertTrue(message.contains("only interfaces found for interface"));
        throw e;
      } else {
        Assert.fail();
      }
    }
  }


  @Test
  public void test002() throws Exception {
    DI.activatePackages("junit.org.rapidpm"); //expliziet activierte pkgs
    final BusinessModule001 businessModule001 = DI.activateDI(new BusinessModule001());
    Assert.assertNotNull(businessModule001);
    Assert.assertNotNull(businessModule001.service);
    Assert.assertTrue(businessModule001.post);
  }


  public static class BusinessModule002 {
    @Inject @Proxy(virtual = true) Service service;

    boolean post = false;

    @PostConstruct
    public void postconstruct() {
      post = true;
    }
  }


  @Test
  public void test003() throws Exception {
    DI.activatePackages("org.rapidpm");
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModule002 businessModule = DI.activateDI(new BusinessModule002());

    Assert.assertNotNull(businessModule);
    Assert.assertTrue(businessModule.post);

    final Service service = businessModule.service;
    Assert.assertNotNull(service);
    Assert.assertTrue(java.lang.reflect.Proxy.isProxyClass(service.getClass()));
  }


  @Test
  public void test004() throws Exception {
    DI.activatePackages("org.rapidpm");
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModule002 businessModule = DI.activateDI(new BusinessModule002());
    Assert.assertNotNull(businessModule);
    Assert.assertTrue(businessModule.post);
    final Service service = businessModule.service;
    Assert.assertNotNull(service);

    final Optional<Service> optional = checkinternalrealService(service);
    Assert.assertFalse(optional.isPresent());
    final String hoppel = service.doWork("Hoppel");
    Assert.assertNotNull(hoppel);
    Assert.assertTrue(checkinternalrealService(service).isPresent());
    Assert.assertEquals("mocked-Hoppel", hoppel);

  }

  private Optional<Service> checkinternalrealService(final Service service) throws NoSuchFieldException, IllegalAccessException {
    Assert.assertTrue(java.lang.reflect.Proxy.isProxyClass(service.getClass()));

    final InvocationHandler invocationHandler = java.lang.reflect.Proxy.getInvocationHandler(service);
    Assert.assertNotNull(invocationHandler);
    Assert.assertTrue(invocationHandler instanceof VirtualDynamicProxyInvocationHandler);

    final Field serviceStrategyFactory = invocationHandler.getClass().getDeclaredField("serviceStrategyFactory");
    Assert.assertNotNull(serviceStrategyFactory);
    final boolean accessible = serviceStrategyFactory.isAccessible();
    serviceStrategyFactory.setAccessible(true);
    final ServiceStrategyFactoryNotThreadSafe realServiceStrategieFactory = (ServiceStrategyFactoryNotThreadSafe) serviceStrategyFactory.get(invocationHandler);
    serviceStrategyFactory.setAccessible(accessible);

    final Field realServiceField = ServiceStrategyFactoryNotThreadSafe.class.getDeclaredField("service");
    Assert.assertNotNull(realServiceField);
    final boolean accessible1 = realServiceField.isAccessible();
    realServiceField.setAccessible(true);
    final Optional result = Optional.ofNullable(realServiceField.get(realServiceStrategieFactory));
    realServiceField.setAccessible(accessible1);
    return result;
  }


}
