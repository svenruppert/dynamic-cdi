package junit.org.rapidpm.ddi.metrics.foo;

/**
 * Created by b.bosch on 17.02.2016.
 */
public class FooImpl implements FooLeft, FooRight {
  private final String name;

  private FooImpl(String name) {
    this.name = name;
  }

  @Override
  public void getLeft() {


  }

  @Override
  public void getRight() {

  }

  @Override
  public void getBar() {

  }

  public static FooImpl getInstance(String name){
    return new FooImpl(name);
  }
}
