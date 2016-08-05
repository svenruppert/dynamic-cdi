package junit.org.rapidpm.ddi.logging.v003;

import junit.org.rapidpm.ddi.DDIBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsGeneratedProxy;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsLoggingProxy;

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
 * Created by RapidPM - Team on 03.08.16.
 */
public class LoggingV003Test extends DDIBaseTest {

  @Test
  public void test001() throws Exception {
    DI.activateLogging(Service.class);
    try {
      DI.activateDI(Service.class);
      Assert.fail();
    } catch (Exception e) {
      Assert.assertTrue(e.getMessage().contains("to many LoggingProxies for"));
    }
  }

  public interface Service {
  }

  public static class ServiceImpl implements Service {
  }

  @IsGeneratedProxy
  @IsLoggingProxy
  public static class LoggingProxyA implements Service {
  }

  @IsGeneratedProxy
  @IsLoggingProxy
  public class LoggingProxyB implements Service {
  }
}
