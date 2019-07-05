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
package junit.org.rapidpm.ddi.producer.v004;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;

public class ProducerTest004 extends DDIBaseTest {


  private static boolean producerUssed;
  @Inject Service service;

  @Test
  public void test001() {
    DI.activateDI(this);
    Assertions.assertEquals(service.getClass(), ServiceImpl.class);
    Assertions.assertTrue(producerUssed);
  }

  public interface Service{}

  public static class ServiceImpl implements Service{}

  @Produces(Service.class)
  public static class ServiceProducer implements Producer<Service>{
    @Override
    public Service create() {
      producerUssed = true;
      return new ServiceImpl();
    }
  }

  @Produces(ServiceImpl.class)
  public static class ServiceImplProducer implements Producer<Service>{
    @Override
    public Service create() {
       throw new RuntimeException("wrong producer activated");
    }
  }


}
