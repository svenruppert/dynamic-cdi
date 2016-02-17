package junit.org.rapidpm.ddi.metrics.foo;

import org.rapidpm.ddi.Produces;
import org.rapidpm.ddi.producer.Producer;

/**
 * Created by b.bosch on 17.02.2016.
 */
@Produces(FooLeft.class)
public class FooLeftProducer implements Producer<FooLeft>{
  @Override
  public FooLeft create() {
    return FooImpl.getInstance("BarLeft");
  }
}
