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
package junit.com.svenruppert.ddi.classresolver.v012;

import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;

import javax.inject.Inject;

public class ClassResolver012Test
    extends DDIBaseTest {


  @Test()
  public void test001() {
    Service service = new Service();
    Assertions.assertThrows(RuntimeException.class, ()-> DI.activateDI(service));
  }

  public interface SubService {
  }

  public static class Service {
    @Inject SubService subService;
  }

  public static class FaultySubService implements SubService {
    public FaultySubService() {
      throw new RuntimeException("OOPS");
    }
  }
}
