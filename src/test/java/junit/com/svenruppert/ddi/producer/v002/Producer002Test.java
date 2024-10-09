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
package junit.com.svenruppert.ddi.producer.v002;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.Produces;
import com.svenruppert.ddi.producer.Producer;

import javax.inject.Inject;

public class Producer002Test
    extends DDIBaseTest {

  @Test()
  public void testProducer001() {
    final BusinessModul businessModul = new BusinessModul();
    try {
      DI.activateDI(businessModul);
    } catch (Exception e) {
      final String msg = e.getMessage();
      System.out.println("msg = " + msg);
      Assertions.assertTrue(msg.contains("to many Producer and no ProducerResolver found for interface"));
    }
  }


  public interface Service {
    String workOn(String txt);
  }

  @Produces(Service.class)
  public static class ServiceProducer01 implements Producer<Service> {
    public Service create() {
      return txt -> txt + "_" + ServiceProducer01.class.getSimpleName();
    }
  }

  @Produces(Service.class)
  public static class ServiceProducer02 implements Producer<Service> {
    public Service create() {
      return txt -> txt + "_" + ServiceProducer01.class.getSimpleName();
    }
  }

  public static class BusinessModul {
    @Inject Service service;

    public String doIt(String txt) {
      return service.workOn(txt);
    }
  }

}
