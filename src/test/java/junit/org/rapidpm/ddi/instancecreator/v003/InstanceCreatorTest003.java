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
package junit.org.rapidpm.ddi.instancecreator.v003;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.producer.InstanceCreator;
import org.rapidpm.ddi.producer.Producer;
import org.rapidpm.ddi.producerresolver.ProducerResolver;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by RapidPM - Team on 02.08.16.
 */
public class InstanceCreatorTest003 extends DDIBaseTest {


  @Test
  public void test001() {
    final ServiceImpl instantiate;
    try {
      instantiate = new InstanceCreator().instantiate(ServiceImpl.class);
      Assertions.fail("too bad..");
    } catch (DDIModelException e) {
      Assertions.assertTrue(e.getMessage().contains("toooo many ProducerResolver for interface/class"));
    }
  }

  public static class ServiceImpl {
  }


  @Produces(ServiceImpl.class)
  public static class ServiceProducer implements Producer<ServiceImpl> {
    @Override
    public ServiceImpl create() {
      return new ServiceImpl() {
      };
    }
  }

  @Produces(ServiceImpl.class)
  public static class ServiceImplProducer implements Producer<ServiceImpl> {
    @Override
    public ServiceImpl create() {
      return new ServiceImpl();
    }
  }

  @ResponsibleFor(ServiceImpl.class)
  public static class ServiceProducerResolver implements ProducerResolver<ServiceImpl,Producer<ServiceImpl>> {

    @Override
    public Class<? extends Producer<ServiceImpl>> resolve(final Class interf) {
      return ServiceProducer.class;
    }
  }

  @ResponsibleFor(ServiceImpl.class)
  public static class ServiceProducerAResolver implements ProducerResolver<ServiceImpl,Producer<ServiceImpl>> {

    @Override
    public Class<? extends Producer<ServiceImpl>> resolve(final Class interf) {
      return ServiceProducer.class;
    }
  }

}
