package junit.org.rapidpm.ddi.reflectionmodel.v002;

import junit.org.rapidpm.ddi.reflectionmodel.v002.api.Service;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.reflections.util.ClasspathHelper;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Created by svenruppert on 12.10.15.
 */
public class ReflectionModelTest002 {


  private static final Collection<URL> urls = ClasspathHelper.forClassLoader();

  @Before
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
  }

  @After
  public void tearDown() throws Exception {
    DI.clearReflectionModel();
  }

  @Test
  public void test001() throws Exception {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(aPackageName, urls);
    Assert.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    DI.clearReflectionModel();
    Assert.assertFalse(DI.isPkgPrefixActivated(aPackageName));

    DI.activatePackages(aPackageName, ClasspathHelper.forClass(Service.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  @Test
  public void test002_a() throws Exception {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(true, aPackageName, urls);
    Assert.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    DI.clearReflectionModel();
    Assert.assertFalse(DI.isPkgPrefixActivated(aPackageName));

    DI.activatePackages(true, aPackageName, ClasspathHelper.forClass(Service.class));
    Assert.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }
  @Test
  public void test002_b() throws Exception {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(false, aPackageName, urls);
    Assert.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  @Test
  public void test003_a() throws Exception {
    final String aPackageName = checkPkgIsNotActivated();


    DI.activatePackages(false, aPackageName);
    Assert.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  @Test
  public void test003_b() throws Exception {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(true, aPackageName);
    Assert.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);


  }

  private void checkTimestamp(final String aPackageName) {
    final LocalDateTime pkgPrefixActivatedTimestamp = DI.getPkgPrefixActivatedTimestamp(aPackageName);
    Assert.assertTrue(pkgPrefixActivatedTimestamp.isBefore(LocalDateTime.now().plusSeconds(1)));
  }

  private String checkPkgIsNotActivated() {
    final Package aPackage = Service.class.getPackage();
    final String aPackageName = aPackage.getName();
    System.out.println("aPackage = " + aPackageName);
    Assert.assertFalse(DI.isPkgPrefixActivated(aPackageName));
    Assert.assertFalse(urls.isEmpty());
    return aPackageName;
  }
}
