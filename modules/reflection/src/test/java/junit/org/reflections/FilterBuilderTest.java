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
package junit.org.reflections;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.util.FilterBuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test filtering
 */
public class FilterBuilderTest {

  @Test
  public void test_include() {
    FilterBuilder filter = new FilterBuilder().include("org\\.reflections.*");
    assertTrue(filter.apply("org.reflections.Reflections"));
    assertTrue(filter.apply("org.reflections.foo.Reflections"));
    assertFalse(filter.apply("org.foobar.Reflections"));
  }

  @Test
  public void test_includePackage() {
    FilterBuilder filter = new FilterBuilder().includePackage("org.reflections");
    assertTrue(filter.apply("org.reflections.Reflections"));
    assertTrue(filter.apply("org.reflections.foo.Reflections"));
    assertFalse(filter.apply("org.foobar.Reflections"));
  }

  @Test
  public void test_includePackageMultiple() {
    FilterBuilder filter = new FilterBuilder().includePackage("org.reflections" , "org.foo");
    assertTrue(filter.apply("org.reflections.Reflections"));
    assertTrue(filter.apply("org.reflections.foo.Reflections"));
    assertTrue(filter.apply("org.foo.Reflections"));
    assertTrue(filter.apply("org.foo.bar.Reflections"));
    assertFalse(filter.apply("org.bar.Reflections"));
  }

  @Test
  public void test_includePackagebyClass() {
    FilterBuilder filter = new FilterBuilder().includePackage(Reflections.class);
    assertTrue(filter.apply("org.reflections.Reflections"));
    assertTrue(filter.apply("org.reflections.foo.Reflections"));
    assertFalse(filter.apply("org.foobar.Reflections"));
  }

  //-----------------------------------------------------------------------
  @Test
  public void test_exclude() {
    FilterBuilder filter = new FilterBuilder().exclude("org\\.reflections.*");
    assertFalse(filter.apply("org.reflections.Reflections"));
    assertFalse(filter.apply("org.reflections.foo.Reflections"));
    assertTrue(filter.apply("org.foobar.Reflections"));
  }

  @Test
  public void test_excludePackage() {
    FilterBuilder filter = new FilterBuilder().excludePackage("org.reflections");
    assertFalse(filter.apply("org.reflections.Reflections"));
    assertFalse(filter.apply("org.reflections.foo.Reflections"));
    assertTrue(filter.apply("org.foobar.Reflections"));
  }

  @Test
  public void test_excludePackageByClass() {
    FilterBuilder filter = new FilterBuilder().excludePackage(Reflections.class);
    assertFalse(filter.apply("org.reflections.Reflections"));
    assertFalse(filter.apply("org.reflections.foo.Reflections"));
    assertTrue(filter.apply("org.foobar.Reflections"));
  }

  //-----------------------------------------------------------------------
  @Test
  public void test_parse_include() {
    FilterBuilder filter = FilterBuilder.parse("+org.reflections.*");
    assertTrue(filter.apply("org.reflections.Reflections"));
    assertTrue(filter.apply("org.reflections.foo.Reflections"));
    assertFalse(filter.apply("org.foobar.Reflections"));
    assertTrue(filter.apply("org.reflectionsplus.Reflections"));
  }

  @Test
  public void test_parse_include_notRegex() {
    FilterBuilder filter = FilterBuilder.parse("+org.reflections");
    assertFalse(filter.apply("org.reflections.Reflections"));
    assertFalse(filter.apply("org.reflections.foo.Reflections"));
    assertFalse(filter.apply("org.foobar.Reflections"));
    assertFalse(filter.apply("org.reflectionsplus.Reflections"));
  }

  @Test
  public void test_parse_exclude() {
    FilterBuilder filter = FilterBuilder.parse("-org.reflections.*");
    assertFalse(filter.apply("org.reflections.Reflections"));
    assertFalse(filter.apply("org.reflections.foo.Reflections"));
    assertTrue(filter.apply("org.foobar.Reflections"));
    assertFalse(filter.apply("org.reflectionsplus.Reflections"));
  }

  @Test
  public void test_parse_exclude_notRegex() {
    FilterBuilder filter = FilterBuilder.parse("-org.reflections");
    assertTrue(filter.apply("org.reflections.Reflections"));
    assertTrue(filter.apply("org.reflections.foo.Reflections"));
    assertTrue(filter.apply("org.foobar.Reflections"));
    assertTrue(filter.apply("org.reflectionsplus.Reflections"));
  }

  @Test
  public void test_parse_include_exclude() {
    FilterBuilder filter = FilterBuilder.parse("+org.reflections.*, -org.reflections.foo.*");
    assertTrue(filter.apply("org.reflections.Reflections"));
    assertFalse(filter.apply("org.reflections.foo.Reflections"));
    assertFalse(filter.apply("org.foobar.Reflections"));
  }

  //-----------------------------------------------------------------------
  @Test
  public void test_parsePackages_include() {
    FilterBuilder filter = FilterBuilder.parsePackages("+org.reflections");
    assertTrue(filter.apply("org.reflections.Reflections"));
    assertTrue(filter.apply("org.reflections.foo.Reflections"));
    assertFalse(filter.apply("org.foobar.Reflections"));
    assertFalse(filter.apply("org.reflectionsplus.Reflections"));
  }

  @Test
  public void test_parsePackages_include_trailingDot() {
    FilterBuilder filter = FilterBuilder.parsePackages("+org.reflections.");
    assertTrue(filter.apply("org.reflections.Reflections"));
    assertTrue(filter.apply("org.reflections.foo.Reflections"));
    assertFalse(filter.apply("org.foobar.Reflections"));
    assertFalse(filter.apply("org.reflectionsplus.Reflections"));
  }

  @Test
  public void test_parsePackages_exclude() {
    FilterBuilder filter = FilterBuilder.parsePackages("-org.reflections");
    assertFalse(filter.apply("org.reflections.Reflections"));
    assertFalse(filter.apply("org.reflections.foo.Reflections"));
    assertTrue(filter.apply("org.foobar.Reflections"));
    assertTrue(filter.apply("org.reflectionsplus.Reflections"));
  }

  @Test
  public void test_parsePackages_exclude_trailingDot() {
    FilterBuilder filter = FilterBuilder.parsePackages("-org.reflections.");
    assertFalse(filter.apply("org.reflections.Reflections"));
    assertFalse(filter.apply("org.reflections.foo.Reflections"));
    assertTrue(filter.apply("org.foobar.Reflections"));
    assertTrue(filter.apply("org.reflectionsplus.Reflections"));
  }

  @Test
  public void test_parsePackages_include_exclude() {
    FilterBuilder filter = FilterBuilder.parsePackages("+org.reflections, -org.reflections.foo");
    assertTrue(filter.apply("org.reflections.Reflections"));
    assertFalse(filter.apply("org.reflections.foo.Reflections"));
    assertFalse(filter.apply("org.foobar.Reflections"));
  }

}
