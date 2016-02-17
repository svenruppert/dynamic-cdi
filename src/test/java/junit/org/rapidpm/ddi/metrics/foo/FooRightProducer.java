package junit.org.rapidpm.ddi.metrics.foo;

import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.producer.Producer;

/**
 * Created by b.bosch on 17.02.2016.
 */
@Produces(FooRight.class)
public class FooRightProducer implements Producer<FooRight> {
  @Override
  public FooRight create() {
    return FooImpl.getInstance("BarRight");
  }
}
