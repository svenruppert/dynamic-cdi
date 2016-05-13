package junit.org.rapidpm.ddi.logging.v002;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by RapidPM - Team on 11.05.16.
 */
public class LogginV002Test extends DDIBaseTest {

  @Test
  public void test001() throws Exception {

    final String s = new DemoClassV002StaticLoggingProxy()
        .withDelegator(new LoggingDemoClassV002())
        .doWork("");



    DI.activateLogging(DemoClassV002.class);
//    DI.activateLogging(LoggingDemoClassV002.class);

    final DemoClassV002 demoClassV002 = DI.activateDI(DemoClassV002.class);
    final String doWork = demoClassV002.doWork("XX");
    Assert.assertEquals("xxXX", doWork);
    System.out.println(doWork);
  }
}
