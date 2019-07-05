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
package junit.org.rapidpm.ddi.reflectionmodel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;

import javax.inject.Inject;

public class ReflectionModel001Test {

  @Test()
  public void test001() {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    try {
      DI.activateDI(new BusinessModule());
      Assertions.fail("to baad....");
    } catch (DDIModelException e) {
        final String message = e.getMessage();
        Assertions.assertTrue(message.contains("only interfaces found for interface"));
    }
  }


  @Test
  public void test002() {
    DI.clearReflectionModel();
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModule businessModule = DI.activateDI(new BusinessModule());
    Assertions.assertNotNull(businessModule);
    Assertions.assertNotNull(businessModule.service);
  }


  interface Service {
  }

  public static class ServiceImpl implements Service {
  }

  public static class BusinessModule {
    @Inject Service service;
  }


}
