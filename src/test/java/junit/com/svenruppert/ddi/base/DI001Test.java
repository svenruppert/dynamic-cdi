/*
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
package junit.com.svenruppert.ddi.base;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;

import javax.inject.Inject;

public class DI001Test
    extends DDIBaseTest {


  @Test
  public void testInjection001() {
    Service service = new Service();
    DI.activateDI(service);

    Assertions.assertNotNull(service.subService);

    Assertions.assertEquals("SubService test", service.work("test"));

  }


  public static class Service {
    @Inject SubService subService;

    public String work(String txt) {
      return subService.work(txt);
    }
  }

  public static class SubService {
    public String work(final String txt) {
      return "SubService " + txt;
    }
  }

}
