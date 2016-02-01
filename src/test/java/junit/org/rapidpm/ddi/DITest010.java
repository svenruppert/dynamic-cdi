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

package junit.org.rapidpm.ddi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;

public class DITest010 {


  @Test
  public void test001() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
    final Service service = DI.activateDI(Service.class);
    Assert.assertNotNull(service);
    Assert.assertEquals(ServiceImpl.class, service.getClass());
  }


  @After
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
  }

  public interface Service {
  }

  public static class ServiceImpl implements Service {
  }

}
