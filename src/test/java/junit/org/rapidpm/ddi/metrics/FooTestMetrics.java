package junit.org.rapidpm.ddi.metrics;

import junit.org.rapidpm.ddi.metrics.foo.FooLeft;
import junit.org.rapidpm.ddi.metrics.foo.FooRight;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.proxybuilder.core.metrics.RapidPMMetricsRegistry;

/**
 * Created by b.bosch on 17.02.2016.
 */
public class FooTestMetrics {
  @Before
  public void init() {
    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
    DI.activateMetrics(FooLeft.class);
    DI.activateMetrics(FooRight.class);
  }

  @Test
  public void test001() throws Exception {

    final FooRight fooRight = DI.activateDI(FooRight.class);
    final FooLeft fooLeft = DI.activateDI(FooLeft.class);

    for (int i = 0; i < 10; i++) {
      fooLeft.getLeft();
      fooRight.getRight();
    }
    RapidPMMetricsRegistry.getInstance().getMetrics().getMeters();


  }
}

