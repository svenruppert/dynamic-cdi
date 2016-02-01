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

package junit.org.rapidpm.ddi.classresolver;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;

import javax.inject.Inject;

public class ClassResolverTest006 extends DDIBaseTest {


  @Test(expected = DDIModelException.class)
  public void testProducer001() throws Exception {

    final BusinessModule businessModule = new BusinessModule();
    try {
      DI.activateDI(businessModule);
    } catch (DDIModelException e) {
      final String message = e.getMessage();
      System.out.println("message = " + message);
      Assert.assertTrue(message.contains("only interfaces found for interface"));
      throw e;
    }
    Assert.fail();
  }

  public interface Service {
    String work(String txt);
  }

  public static class BusinessModule {
    @Inject Service service;

    public String work(String txt) {
      return service.work(txt);
    }
  }

}
