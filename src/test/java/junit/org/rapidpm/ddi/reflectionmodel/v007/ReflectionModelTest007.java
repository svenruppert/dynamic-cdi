package junit.org.rapidpm.ddi.reflectionmodel.v007;

import junit.org.rapidpm.ddi.reflectionmodel.v007.pkg.PkgServiceA;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.reflections.ReflectionsModel;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

/**
 * Created by Sven Ruppert on 25.01.16.
 */
public class ReflectionModelTest007 {


  @Before
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
  }


  @After
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
  }


  @Test
  public void test001() throws Exception {
    final Field declaredField = DI.class.getDeclaredField("reflectionsModel");

    declaredField.setAccessible(true);
    final ReflectionsModel reflectionModel = (ReflectionsModel) declaredField.get(null);
    final Collection<String> classesForPkg = reflectionModel.getClassesForPkg(PkgServiceA.class.getPackage().getName());
    Assert.assertFalse(classesForPkg.isEmpty());
    Assert.assertEquals(2, classesForPkg.size());


    final Set<String> activatedPkgs = reflectionModel.getActivatedPkgs();
    Assert.assertFalse(activatedPkgs.isEmpty());
    Assert.assertEquals(2, activatedPkgs.size());

  }
}
