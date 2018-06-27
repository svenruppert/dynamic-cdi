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
package junit.org.rapidpm.ddi.classresolver.v017;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;

/**
 *
 */
public class ClassresolverTest017 {


  public static interface Service {}

  public static abstract class AbstractService implements Service {}

  public static class ServiceImpl extends AbstractService {}


  @BeforeEach
  void setUp() {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    DI.activatePackages(this.getClass());
  }

  @AfterEach
  void tearDown() {
    DI.clearReflectionModel();
  }

  @Test
  void test001() {
    final Service service = DI.activateDI(Service.class);
    Assertions.assertEquals(service.getClass() , ServiceImpl.class);
  }

  @Test
  void test002() {
    final Service service = DI.activateDI(ServiceImpl.class);
    Assertions.assertEquals(service.getClass() , ServiceImpl.class);
  }


}
