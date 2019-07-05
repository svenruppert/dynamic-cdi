/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.org.rapidpm.ddi.scopes.v003;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.scopes.InjectionScope;

import java.util.Set;

public class Scopes003Test
    extends DDIBaseTest {

  @Test
  public void test001() {
    final Set<String> scopes = DI.listAllActiveScopes();
    Assertions.assertNotNull(scopes);
    Assertions.assertFalse(scopes.isEmpty());
    Assertions.assertTrue(scopes.contains(TestScopeB.class.getSimpleName()));
    Assertions.assertFalse(scopes.contains(TestScopeA.class.getSimpleName()));
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