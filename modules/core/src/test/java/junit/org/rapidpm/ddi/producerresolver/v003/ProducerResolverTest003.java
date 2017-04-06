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

package junit.org.rapidpm.ddi.producerresolver.v003;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.producer.Producer;
import org.rapidpm.ddi.producerresolver.ProducerResolver;

public class ProducerResolverTest003 extends DDIBaseTest {


  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    try {
      DI.activateDI(MyService.class);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof DDIModelException);
      Assert.assertTrue(e.getMessage().contains("to many producersResolver for Impl"));
//      Assert.assertTrue(e.getMessage().contains("toooo many ProducerResolver for interface/class class"));
      throw e;
    }
  }


  public interface MyService {
    String doWork(String txt);
  }

  public static class MyServiceImpl_A implements MyService {
    @Override
    public String doWork(final String txt) {
      return this.getClass().getSimpleName() + txt;
    }
  }

  @Produces(MyServiceImpl_A.class)
  public static class Producer_A_1 implements Producer<MyServiceImpl_A> {
    @Override
    public MyServiceImpl_A create() {
      return new MyServiceImpl_A();
    }
  }

  @Produces(MyServiceImpl_A.class)
  public static class Producer_A_2 implements Producer<MyServiceImpl_A> {
    @Override
    public MyServiceImpl_A create() {
      return new MyServiceImpl_A();
    }
  }

  @ResponsibleFor(MyServiceImpl_A.class)
  public static class MyProducerResolver_A implements ProducerResolver<MyServiceImpl_A, Producer<MyServiceImpl_A>> {
    @Override
    public Class resolve(final Class<? extends MyServiceImpl_A> interf) {
      return Producer_A_2.class;
    }
  }

  @ResponsibleFor(MyServiceImpl_A.class)
  public static class MyProducerResolver_B implements ProducerResolver<MyServiceImpl_A, Producer<MyServiceImpl_A>> {
    @Override
    public Class resolve(final Class<? extends MyServiceImpl_A> interf) {
      return Producer_A_2.class;
    }
  }
}
