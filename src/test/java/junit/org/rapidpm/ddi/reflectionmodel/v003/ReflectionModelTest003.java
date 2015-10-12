package junit.org.rapidpm.ddi.reflectionmodel.v003;

import org.junit.Test;
import org.rapidpm.ddi.DI;

/**
 * Created by svenruppert on 12.10.15.
 */
public class ReflectionModelTest003 {


  @Test
  public void test001() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    DI.checkActiveModel();
  }
}
