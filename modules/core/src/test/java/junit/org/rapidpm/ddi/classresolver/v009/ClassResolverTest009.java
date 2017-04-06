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

package junit.org.rapidpm.ddi.classresolver.v009;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.Proxy;
import org.rapidpm.ddi.producer.Producer;

import javax.inject.Inject;

public class ClassResolverTest009 extends DDIBaseTest {

  /**
   * 1 Interface, 2 impl, 1 Producer for Interface -> Producer will be used
   *
   * @throws Exception
   */
  @Test
  public void testProxy001() throws Exception {
    final BusinessModul instance = new BusinessModul();
    Assert.assertNotNull(instance);
    Assert.assertNull(instance.service);
    DI.activateDI(instance);

    Assert.assertNotNull(instance.service);

    final String hello = instance.service.doWork("Hello");
    Assert.assertNotNull(hello);
    Assert.assertEquals("created by Producer", hello);
  }

  @Test
  public void testProxy002() throws Exception {
    final BusinessModulVirtual instance = new BusinessModulVirtual();
    Assert.assertNotNull(instance);
    Assert.assertNull(instance.service);
    DI.activateDI(instance);

    Assert.assertNotNull(instance.service);

    Assert.assertTrue(java.lang.reflect.Proxy.isProxyClass(instance.service.getClass()));
    final String hello = instance.service.doWork("Hello");
    Assert.assertNotNull(hello);
    Assert.assertEquals("created by Producer", hello);
  }


  public interface Service {
    String doWork(String str);
  }

  public static class BusinessModul {
    @Inject Service service;

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class BusinessModulVirtual {
    @Inject @Proxy(virtual = true) Service service;

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class ServiceA implements Service {

    public ServiceA() {
      System.out.println(" ServiceA = constructed...");
    }

    @Override
    public String doWork(final String str) {
      return "ServiceA_" + str;
    }
  }


  @Produces(Service.class)
  public static class ServiceProducer implements Producer<Service> {
    @Override
    public Service create() {
      return new Service() { //this is a implementation of the Interface...
        @Override
        public String doWork(final String str) {
          return "created by Producer";
        }
      };
    }
  }

}
