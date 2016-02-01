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
import org.rapidpm.ddi.DI;

import javax.inject.Inject;
import java.util.Set;

public class ClassResolverTest010 extends DDIBaseTest {

@Inject Service service;

  @Test
  public void test001() throws Exception {

    final Set<Class<? extends Service>> subTypesOf = DI.getSubTypesOf(Service.class);
    for (Class<? extends Service> aClass : subTypesOf) {
      System.out.println("aClass = " + aClass);
      System.out.println("aClass.isInterface() = " + aClass.isInterface());


    }



    DI.activateDI(this);
    Assert.assertNotNull(service);

  }

  public interface Service {
    String doWork(String str);
  }
  public interface ServiceA extends Service {
    String doWork(String str);
  }
  public interface ServiceB extends Service  {
    String doWork(String str);
  }
  public interface ServiceAA extends ServiceA  {
    String doWork(String str);
  }

  public static class ServiceImpl implements ServiceAA{

    @Override
    public String doWork(final String str) {
      return "ServiceImpl " + str;
    }
  }


}
