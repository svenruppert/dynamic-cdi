/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.com.svenruppert.ddi.reflectionmodel.v002;

import junit.com.svenruppert.ddi.reflectionmodel.v002.api.Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.svenruppert.ddi.DI;
import org.reflections8.util.ClasspathHelper;

import java.time.LocalDateTime;

public class ReflectionModel002Test {


//  private static final Collection<URL> urls = ClasspathHelper.forClassLoader();

  @BeforeEach
  public void setUp() {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
  }

  @AfterEach
  public void tearDown() {
    DI.clearReflectionModel();
  }

  @Test
  public void test001() {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(aPackageName, ClasspathHelper.forClassLoader());
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    DI.clearReflectionModel();
    Assertions.assertFalse(DI.isPkgPrefixActivated(aPackageName));

    DI.activatePackages(aPackageName, ClasspathHelper.forClass(Service.class));
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  private String checkPkgIsNotActivated() {
    final Package aPackage = Service.class.getPackage();
    final String aPackageName = aPackage.getName();
    Assertions.assertFalse(DI.isPkgPrefixActivated(aPackageName));
    return aPackageName;
  }

  private void checkTimestamp(final String aPackageName) {
    final LocalDateTime pkgPrefixActivatedTimestamp = DI.getPkgPrefixActivatedTimestamp(aPackageName);
    Assertions.assertTrue(pkgPrefixActivatedTimestamp.isBefore(LocalDateTime.now().plusSeconds(1)));
  }

  @Test
  public void test002_a() {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(aPackageName, ClasspathHelper.forClassLoader());
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    DI.clearReflectionModel();
    Assertions.assertFalse(DI.isPkgPrefixActivated(aPackageName));

    DI.activatePackages(aPackageName, ClasspathHelper.forClass(Service.class));
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  @Test
  public void test002_b() {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages( aPackageName, ClasspathHelper.forClassLoader());
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  @Test
  public void test003_a() {
    final String aPackageName = checkPkgIsNotActivated();


    DI.activatePackages(aPackageName);
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);
  }

  @Test
  public void test003_b() {
    final String aPackageName = checkPkgIsNotActivated();

    DI.activatePackages(aPackageName);
    Assertions.assertTrue(DI.isPkgPrefixActivated(aPackageName));

    checkTimestamp(aPackageName);


  }
}
