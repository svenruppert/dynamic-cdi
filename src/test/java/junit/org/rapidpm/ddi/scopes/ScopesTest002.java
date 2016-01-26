package junit.org.rapidpm.ddi.scopes;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.scopes.InjectionScope;
import org.rapidpm.ddi.scopes.provided.JVMSingletonInjectionScope;

/**
 * Created by Sven Ruppert on 25.01.16.
 */
public class ScopesTest002 extends DDIBaseTest {

  @Test
  public void test001() throws Exception {

    DI.registerClassForScope(SingletonTestClass.class, JVMSingletonInjectionScope.class.getSimpleName());
    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assert.assertNotNull(serviceA);
    Assert.assertNotNull(serviceB);
    Assert.assertTrue(serviceA instanceof SingletonTestClass);
    Assert.assertTrue(serviceB instanceof SingletonTestClass);

    Assert.assertEquals(serviceA, serviceB); // Singleton at impl level

  }

  @Test
  public void test002() throws Exception {

    DI.registerClassForScope(Service.class, JVMSingletonInjectionScope.class.getSimpleName());
    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assert.assertNotNull(serviceA);
    Assert.assertNotNull(serviceB);
    Assert.assertTrue(serviceA instanceof SingletonTestClass);
    Assert.assertTrue(serviceB instanceof SingletonTestClass);

    Assert.assertEquals(serviceA, serviceB); // Singleton at impl level

  }


  public interface Service {

  }


  public static class SingletonTestClass implements Service {
  }


  public static class TestScope extends InjectionScope {
    @Override
    public <T> T getInstance(final String clazz) {
      return null;
    }

    @Override
    public <T> void storeInstance(final Class<T> targetClassOrInterface, final T instance) {

    }

    @Override
    public void clear() {

    }

    @Override
    public String getScopeName() {
      return TestScope.class.getSimpleName();
    }
  }


}
