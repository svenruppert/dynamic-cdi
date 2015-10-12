package junit.org.rapidpm.ddi;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.rapidpm.ddi.DI;

/**
 * Created by svenruppert on 15.08.15.
 */
public class DDIBaseTest {


  @Before
  public void setUpDDI() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    final String name = this.getClass().getPackage().getName();
    System.out.println("name = " + name);
    DI.activatePackages(name);
  }

  @After
  public void tearDownDDI() throws Exception {
    DI.clearReflectionModel();
  }
}
