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
package junit.org.rapidpm.ddi.reflections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.reflections.ReflectionUtils;

/**
 * ReflectionUtils Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jan 25, 2016</pre>
 */
public class ReflectionUtilsTest {

  @BeforeEach
  public void before() throws Exception {
  }

  @AfterEach
  public void after() throws Exception {
  }

  /**
   * Method: checkInterface(final Type aClass, Class targetInterface)
   */
  @Test
  public void testCheckInterface() throws Exception {

    final ReflectionUtils utils = new ReflectionUtils();

    Assertions.assertTrue(utils.checkInterface(ServiceImpl_A.class, Service.class));
    Assertions.assertFalse(utils.checkInterface(Service.class, ServiceImpl_A.class));

    Assertions.assertFalse(utils.checkInterface(ServiceImpl_A.class, NoService.class));
    Assertions.assertFalse(utils.checkInterface(NoService.class, ServiceImpl_A.class));


    Assertions.assertFalse(utils.checkInterface(NoService.class, Service.class));
    Assertions.assertFalse(utils.checkInterface(Service.class, NoService.class));

    Assertions.assertFalse(utils.checkInterface(Service.class, ServiceLev2.class));
    Assertions.assertTrue(utils.checkInterface(ServiceLev2.class, Service.class));

    Assertions.assertTrue(utils.checkInterface(ServiceImpl_B.class, Service.class));
    Assertions.assertFalse(utils.checkInterface(Service.class, ServiceImpl_B.class));

    Assertions.assertFalse(utils.checkInterface(ServiceImpl_B.class, NoService.class));
    Assertions.assertFalse(utils.checkInterface(NoService.class, ServiceImpl_B.class));

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
