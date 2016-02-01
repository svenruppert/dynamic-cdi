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

package junit.org.rapidpm.ddi.reflections.v001;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.reflections.ReflectionUtils;
import org.rapidpm.proxybuilder.core.metrics.RapidPMMetricsRegistry;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsMetricsProxy;

public class ReflectionUtilsTest001 {


  @Test(expected = DDIModelException.class)
  public void test001() throws Exception {
    final ReflectionUtils utils = new ReflectionUtils();
    utils.setDelegatorToMetrixsProxy(new TestMetricsServiceStaticMetricsProxy() {
    }, new TestMetricsServiceImpl());
  }


  public static class TestMetricsServiceImpl implements TestMetricsService {
    @Override
    public String doWork(final String txt) {
      return "huhu";
    }
  }


  @IsMetricsProxy
  public abstract static class TestMetricsServiceStaticMetricsProxy implements TestMetricsService {
    private static final String CLASS_NAME = "junit.org.rapidpm.ddi.reflections.v001.TestMetricsService";
    private final MetricRegistry metrics = RapidPMMetricsRegistry.getInstance().getMetrics();
    private TestMetricsService delegator;

    //this will be expected

//    public TestMetricsServiceStaticMetricsProxy withTestMetricsService(final TestMetricsService delegator) {
//      this.delegator = delegator;
//      return this;
//    }

    public TestMetricsServiceStaticMetricsProxy wTestMetricsService(final TestMetricsService delegator) {
      this.delegator = delegator;
      return this;
    }

    public String doWork(final String txt) {
      final long start = System.nanoTime();
      String result = delegator.doWork(txt);
      final long stop = System.nanoTime();
      final Histogram methodCalls = metrics.histogram(CLASS_NAME + "." + "doWork");
      methodCalls.update(stop - start);
      return result;
    }
  }
}
