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

package junit.org.rapidpm.ddi.proxy;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.Proxy;

import javax.inject.Inject;

public class ProxyTest002 extends DDIBaseTest {


  @Test
  public void test001() throws Exception {
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModul businessModul = new BusinessModul();
    DI.activateDI(businessModul);

    Assert.assertNotNull(businessModul);
    Assert.assertNotNull(businessModul.service);
  }

  @Test
  public void test002() throws Exception {
    DI.activatePackages("junit.org.rapidpm");
    final BusinessModulSecure businessModul = new BusinessModulSecure();
    DI.activateDI(businessModul);

    Assert.assertNotNull(businessModul);
    Assert.assertNotNull(businessModul.service);
  }

  public interface Service {
    String doWork(String str);
  }

  public static class BusinessModulSecure {
    @Inject @Proxy(virtual = true, metrics = true) Service service;

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class BusinessModul {
    @Inject @Proxy(secure = false) Service service;

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

}
