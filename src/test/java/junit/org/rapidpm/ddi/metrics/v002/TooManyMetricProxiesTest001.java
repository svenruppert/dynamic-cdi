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

package junit.org.rapidpm.ddi.metrics.v002;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.DI;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsMetricsProxy;

public class TooManyMetricProxiesTest001 {


  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
    DI.activateMetrics(MetricsService.class);

    try {
      DI.activateDI(MetricsService.class);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertEquals(DDIModelException.class, e.getClass());
      Assert.assertTrue(e.getMessage().contains("to many MetricProxies for interface"));
      throw e;
    }


  }

  public interface MetricsService {
  }

  @IsMetricsProxy
  public interface Proxy01 extends MetricsService {
  }

  @IsMetricsProxy
  public interface Proxy02 extends MetricsService {
  }

  public static class MetricsServiceImpl implements MetricsService {
  }
}
