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
package junit.com.svenruppert.ddi.producer.v003;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.Produces;
import com.svenruppert.ddi.producer.Producer;

import javax.inject.Inject;

public class Producer003Test
    extends DDIBaseTest {


  @Test
  public void testProducer001() {
    final BusinessModul businessModul = new BusinessModul();
    DI.activateDI(businessModul);

    Assertions.assertNotNull(businessModul);
    Assertions.assertNotNull(businessModul.service);
    Assertions.assertNotNull(businessModul.service.workOn("AEAE"));
    Assertions.assertEquals("AEAE_" + ServiceProducer.class.getSimpleName(), businessModul.service.workOn("AEAE"));

  }


  public interface Service<T> {
    T workOn(T txt);
  }

  @Produces(Service.class)
  public static class ServiceProducer implements Producer<Service<String>> {
    public Service<String> create() {
      return txt -> txt + "_" + ServiceProducer.class.getSimpleName();
    }
  }

  public static class BusinessModul {
    @Inject Service service;

    public String doIt(String txt) {
      return (String) service.workOn(txt);
    }
  }

}