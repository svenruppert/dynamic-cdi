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

package junit.org.rapidpm.ddi.classresolver.v007;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;

import javax.inject.Inject;

public class ClassResolverTest007 extends DDIBaseTest {


  /**
   * Anonymous Class inside the create() Method.. will be removed
   *
   * 1 Interface , 1 Impl -> Impl ServiceA will be used
   *
   * @throws Exception
   */
  @Test(expected = DDIModelException.class)
  public void testProxy001() throws Exception {
    final BusinessModulVirtual instance = new BusinessModulVirtual();
    Assert.assertNotNull(instance);
    Assert.assertNull(instance.service);
    DI.activateDI(instance);
    Assert.assertEquals("created by Anonymous", instance.work(""));
  }


  public interface Service {
    String doWork(String str);
  }

  public static class BusinessModulVirtual {
    @Inject Service service;

    public String work(String str) {
      return service.doWork(str);
    }
  }

  public static class BusinessModul {
    private final Service service = new Service() { //this is a implementation of the Interface...
      @Override
      public String doWork(final String str) {
        return "created by Anonymous";
      }
    };

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
