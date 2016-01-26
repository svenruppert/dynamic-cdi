package junit.org.rapidpm.ddi.scopes;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.scopes.InjectionScope;

import java.util.Set;

/**
 * Created by Sven Ruppert on 25.01.16.
 */
public class ScopesTest003 extends DDIBaseTest {

  @Test
  public void test001() throws Exception {
    final Set<String> scopes = DI.listAllActiveScopes();
    Assert.assertNotNull(scopes);
    Assert.assertFalse(scopes.isEmpty());
    Assert.assertTrue(scopes.contains(TestScopeB.class.getSimpleName()));
    Assert.assertFalse(scopes.contains(TestScopeA.class.getSimpleName()));
  }


  public interface Service {
  }


  public static class SingletonTestClass implements Service {
  }

  public static class TestScopeA extends InjectionScope {

    //no default constructor
    public TestScopeA(String txt) {
    }

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
      return TestScopeA.class.getSimpleName();
    }
  }

  public static class TestScopeB extends InjectionScope {

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
      return TestScopeB.class.getSimpleName();
    }
  }


}