package junit.org.rapidpm.ddi;

import org.junit.BeforeClass;
import org.rapidpm.ddi.DI;
import org.reflections.util.ClasspathHelper;

import java.net.URL;
import java.util.Collection;

/**
 * Created by svenruppert on 15.08.15.
 */
public class DDIBaseTest {


  @BeforeClass
  public static void setUpClass() throws Exception {
    DI.activatePackages("junit.org.rapidpm");
  }

}
