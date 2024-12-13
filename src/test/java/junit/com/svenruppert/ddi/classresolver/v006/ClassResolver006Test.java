/*
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
package junit.com.svenruppert.ddi.classresolver.v006;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DDIModelException;
import com.svenruppert.ddi.DI;

import javax.inject.Inject;

public class ClassResolver006Test
    extends DDIBaseTest {


  @Test()
  public void testProducer001() {

    final BusinessModule businessModule = new BusinessModule();
    try {
      DI.activateDI(businessModule);
      Assertions.fail("too bad..");
    } catch (DDIModelException e) {
      final String message = e.getMessage();
      System.out.println("message = " + message);
      Assertions.assertTrue(message.contains("only interfaces found for interface"));
    }

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
