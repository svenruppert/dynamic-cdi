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
package junit.org.rapidpm.ddi.producer.v001;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;

public class ProducerTest001 extends DDIBaseTest {


  @Test
  public void testProducer001() throws Exception {
    final BusinessModul businessModul = new BusinessModul();
    DI.activateDI(businessModul);

    Assertions.assertNotNull(businessModul);
    Assertions.assertNotNull(businessModul.service);
    Assertions.assertNotNull(businessModul.service.workOn("AEAE"));
    Assertions.assertEquals("AEAE_" + ServiceProducer.class.getSimpleName(), businessModul.service.workOn("AEAE"));

  }


  public interface Service {
    String workOn(String txt);
  }

  @Produces(Service.class)
  public static class ServiceProducer implements Producer<Service> {
    public Service create() {
      return txt -> txt + "_" + ServiceProducer.class.getSimpleName();
    }
  }

  public static class BusinessModul {
    @Inject Service service;

    public String doIt(String txt) {
      return service.workOn(txt);
    }
  }

}
