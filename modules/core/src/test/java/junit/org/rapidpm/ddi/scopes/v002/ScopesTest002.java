/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package junit.org.rapidpm.ddi.scopes.v002;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.scopes.InjectionScope;
import org.rapidpm.ddi.scopes.provided.JVMSingletonInjectionScope;

public class ScopesTest002 extends DDIBaseTest {

  @Test
  public void test001() throws Exception {

    DI.registerClassForScope(SingletonTestClass.class, JVMSingletonInjectionScope.class.getSimpleName());
    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);
    Assertions.assertTrue(serviceA instanceof SingletonTestClass);
    Assertions.assertTrue(serviceB instanceof SingletonTestClass);

    Assertions.assertEquals(serviceA, serviceB); // Singleton at impl level

  }

  @Test
  public void test002() throws Exception {

    DI.registerClassForScope(Service.class, JVMSingletonInjectionScope.class.getSimpleName());
    final Service serviceA = DI.activateDI(Service.class);
    final Service serviceB = DI.activateDI(Service.class);

    Assertions.assertNotNull(serviceA);
    Assertions.assertNotNull(serviceB);
    Assertions.assertTrue(serviceA instanceof SingletonTestClass);
    Assertions.assertTrue(serviceB instanceof SingletonTestClass);

    Assertions.assertEquals(serviceA, serviceB); // Singleton at impl level

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
