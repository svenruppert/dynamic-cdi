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
package junit.com.svenruppert.ddi.producerresolver.v002;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.Produces;
import com.svenruppert.ddi.ResponsibleFor;
import com.svenruppert.ddi.implresolver.ClassResolver;
import com.svenruppert.ddi.producer.Producer;
import com.svenruppert.ddi.producerresolver.ProducerResolver;

import javax.inject.Inject;

public class ProducerResolver002Test
    extends DDIBaseTest {

  static boolean producer_a_1;
  static boolean producer_a_2;
  @Inject MyService myService;

  @Test
  public void test001() {
    DI.activateDI(this);
    Assertions.assertNotNull(myService);
    final String s = "Hello";
    Assertions.assertEquals(MyServiceImpl_A.class.getSimpleName() + s, myService.doWork(s));
    Assertions.assertFalse(producer_a_1);
    Assertions.assertTrue(producer_a_2);

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

  public static class MyServiceImpl_B implements MyService {
    @Override
    public String doWork(final String txt) {
      return this.getClass().getSimpleName() + txt;
    }
  }

  @ResponsibleFor(MyService.class)
  public static class MyServiceImplResolver implements ClassResolver<MyService> {

    @Override
    public Class<? extends MyService> resolve(final Class<MyService> interf) {
      System.out.println("MyServiceImplResolver.resolve = " + interf);
      return MyServiceImpl_A.class;
    }
  }

  @Produces(MyServiceImpl_A.class)
  public static class Producer_A_1 implements Producer<MyServiceImpl_A> {
    @Override
    public MyServiceImpl_A create() {
      producer_a_1 = true;
      return new MyServiceImpl_A();
    }
  }

  @Produces(MyServiceImpl_A.class)
  public static class Producer_A_2 implements Producer<MyServiceImpl_A> {
    @Override
    public MyServiceImpl_A create() {
      producer_a_2 = true;
      return new MyServiceImpl_A();
    }
  }

  @ResponsibleFor(MyServiceImpl_A.class)
  public static class MyProducerResolver implements ProducerResolver<MyServiceImpl_A,Producer<MyServiceImpl_A>> {
    @Override
    public Class resolve(final Class<? extends MyServiceImpl_A> interf) {
      System.out.println("MyProducerResolver.resolve = " + interf);
      return Producer_A_2.class;
    }
  }
}
