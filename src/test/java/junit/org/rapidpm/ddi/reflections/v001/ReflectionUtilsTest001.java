package junit.org.rapidpm.ddi.reflections.v001;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import org.junit.Test;
import org.rapidpm.ddi.DDIModelException;
import org.rapidpm.ddi.reflections.ReflectionUtils;
import org.rapidpm.proxybuilder.core.metrics.RapidPMMetricsRegistry;
import org.rapidpm.proxybuilder.staticgenerated.annotations.IsMetricsProxy;

/**
 * Created by Sven Ruppert on 25.01.16.
 */
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
